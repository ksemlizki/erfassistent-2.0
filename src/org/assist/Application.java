package org.assist;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.assist.components.main.MainFrame;
import org.assist.tools.CurrencyDownload;
import org.assist.tools.Tools;

public class Application {

	public static void main(String[] args) throws Exception {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

				@Override
				public void uncaughtException(Thread thread, Throwable throwable) {
					Tools.log(throwable);
				}
			});

			final MainFrame mainFrame = MainFrame.getInstance();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					CurrencyDownload.fillDb();
					mainFrame.setVisible(true);
				}
			});

		}
		catch (Exception exception) {
			Tools.log(exception);
		}
	}

}