package org.assist.tools;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.assist.constants.GeneralConstants;


public class CurrencyDownload {



	public static void main(String[] args) throws Exception {
		System.out.println(getCurrencyRate("USD", new Date()));
	}

	public static ArrayList<String> getCurrencyTokens()  {
		ArrayList<String> tokens = new ArrayList<>();
		tokens.add(GeneralConstants.DEFAULT_CURRENCY);
		Connection connection = getConnection(false);
		try {
			ResultSet resultSet = connection.createStatement().executeQuery("select TOKEN from CURRENCY_TOKEN where  ALLOWED= true");
			while (resultSet.next()) {
				tokens.add(resultSet.getString("TOKEN"));
			}
		}
		catch (SQLException exception) {
			exception.printStackTrace();
		}

		return tokens;
	}

	public static BigDecimal getCurrencyRate(String currency, Date date)  {
		try {
			Connection connection = getConnection(true);

			if (connection == null) {
				fillDb();
				connection = getConnection(true);
			}

			PreparedStatement statement = connection.prepareStatement("SELECT top 1 "+currency+" FROM CURRENCY where RATE_DATE <= ? order by RATE_DATE desc ");
			statement.setDate(1, new java.sql.Date(date.getTime()));
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getBigDecimal(1);
			}
		}
		catch (SQLException exception) {
			Tools.log(exception);
		}
		return null;
	}


	private static Connection getConnection(boolean ifExists) {
		try {
			Class.forName("org.h2.Driver");
			String userdir = System.getProperty("user.dir");
			try {
				String url = "jdbc:h2:file:"+userdir +"/currency";
				if (ifExists) {
					url+=";IFEXISTS=TRUE";
				}
				url+=";AUTO_SERVER=TRUE";
				return  DriverManager.getConnection(url, "sa", "");
			}
			catch (SQLException exception) {
				exception.printStackTrace();
				return null;
			}

		}
		catch (ClassNotFoundException exception) {
			exception.printStackTrace();
		}

		return null;
	}

	public static void fillDb()  {


		try {

			InetAddress address = InetAddress.getLocalHost();
			Connection connection = getConnection(false);
			Statement statement = connection.createStatement();

			String userdir = System.getProperty("user.dir");
			if (!"W602-Semlizk-Ko".equals(address.getHostName())) {
				URL website = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip");
				ReadableByteChannel rbc = Channels.newChannel(website.openStream());
				FileOutputStream fos = new FileOutputStream(userdir + "/eurofxref.zip");
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				fos.close();
			}
			ZipFile zipFile = new ZipFile(userdir + "/eurofxref.zip");
			ZipEntry entry = zipFile.getEntry("eurofxref-hist.csv");

			InputStream inputStream = zipFile.getInputStream(entry);

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			String line = null;
			line = reader.readLine();

			String[] columns = line.split(",");

			StringBuilder insertSql = new StringBuilder("MERGE INTO CURRENCY (RATE_DATE");
			StringBuilder valuesPart = new StringBuilder();

			StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS CURRENCY (RATE_DATE date primary key");
			ArrayList<String> currencyTokens = new ArrayList<>();
			for (int i = 1; i < columns.length; i++) {
				String string = columns[i];
				currencyTokens.add(string);
				sql.append(", ").append(string).append(" decimal(12,4) NULL");
				insertSql.append(", ").append(string);
				valuesPart.append(",? ");
			}
			sql.append(")");
			insertSql.append(")   VALUES (?").append(valuesPart.toString()).append(")");

			statement.execute("DROP TABLE IF EXISTS CURRENCY");
			statement.execute(sql.toString());

			PreparedStatement statement2 = connection.prepareStatement(insertSql.toString());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DecimalFormat decimalFormat = new DecimalFormat("##,####");
			while((line = reader.readLine()) != null) {
				String[] values = line.split(",");

				for (int i = 0; i < values.length; i++) {
					String string = values[i];
					if (i == 0) {
						statement2.setDate(1, new java.sql.Date(dateFormat.parse(string).getTime()));
					}
					else {
						if (string.equals("N/A")) {
							statement2.setNull(i+1, Types.DECIMAL);
						}
						else {
							statement2.setBigDecimal(i+1, BigDecimal.valueOf(decimalFormat.parse(string.replace('.', ',')).doubleValue()));
						}

					}
				}
				statement2.execute();
			}
			reader.close();
			zipFile.close();

			ResultSet rset = connection.getMetaData().getTables(null, null, "CURRENCY_TOKEN", null);
			if (!rset.next()) {
				statement.execute("CREATE TABLE IF NOT EXISTS  CURRENCY_TOKEN(TOKEN VARCHAR(5) primary key, ALLOWED BOOLEAN)");
				for (String string : currencyTokens) {
					boolean allowed = string.equalsIgnoreCase("USD")
							||string.equalsIgnoreCase("CHF")
							||string.equalsIgnoreCase("GBP")
							||string.equalsIgnoreCase("PLN")
							||string.equalsIgnoreCase("CZK")
							||string.equalsIgnoreCase("RUB")
							;

					statement.execute("INSERT INTO CURRENCY_TOKEN values('"+string+"',"+(Boolean.toString(allowed))+")");

				}
			}

		}
		catch (Exception exception) {
			Tools.log(exception);
		}
	}

}
