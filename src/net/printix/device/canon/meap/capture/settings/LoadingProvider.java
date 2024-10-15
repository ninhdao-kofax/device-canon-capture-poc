// ------------------------------------------------------------------------------
// Copyright (c) 2002-2024 Tungsten Automation. All rights reserved.
// Description: LoadingProvider
// ------------------------------------------------------------------------------
package net.printix.device.canon.meap.capture.settings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedString;
import java.util.Locale;
import javax.imageio.ImageIO;

import net.printix.device.canon.meap.capture.log.Logger;
import net.printix.device.canon.meap.capture.util.Util;

public class LoadingProvider {
	
	private static final String TAG = LoadingProvider.class.getSimpleName();
	private static final String BUNDLE_LOADING_FILE = "Loading";
	private static String languageCode;

	/**
	 * Generate the loading background with the localized text
	 * @param appletSize
	 * @return
	 */
	public static Image getLoadingImage(Dimension appletSize) {
		Logger.d(TAG, "getLoadingImage starting");
		Image image = drawTextOntoImage(
				Util.getLocalizedText(AppConstants.LOADING_TEXT, Locale.getDefault()),
				(int) appletSize.getWidth(), (int) appletSize.getHeight());
		Logger.d(TAG, "getLoadingImage localized text with locale: " + Locale.getDefault());
		return image;
	}

	public static boolean isLanguageChanged() {
        String currentLang = getLanguageCode(Locale.getDefault());
		return !(languageCode != null && !languageCode.isEmpty() && languageCode.equals(currentLang));
	}

    private static String getLanguageCode(Locale locale) {
        if (locale.toString().contains("zh_CN")) {
            return "zh-cn";
        } else if (locale.toString().contains("zh_TW")) {
            return "zh-tw";
        } else
            return locale.getLanguage();
    }

	/*
	 * Cloned from the same method from meap_login ImageUtil.java
	 */
	private static Image drawTextOntoImage(String text, int width, int height) {

		Image image = null;
		BufferedImage bufferedImage = null;
		ByteArrayOutputStream baos = null;
		InputStream is = null;

		try {
			// bufferedImage = ImageIO.read(file);
			bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			Font font = new Font("Times New Roman", Font.BOLD, 46);

			AttributedString attributedString = new AttributedString(text);
			attributedString.addAttribute(TextAttribute.FONT, font);
			attributedString.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);

			Graphics2D graphics = bufferedImage.createGraphics();
			graphics.setBackground(Color.WHITE);
			graphics.clearRect(0, 0, width, height);

			FontMetrics metrics = graphics.getFontMetrics(font);

			// align text to center of image
			int positionX = (bufferedImage.getWidth() - metrics.stringWidth(text)) / 2;
			int positionY = (bufferedImage.getHeight() - metrics.getHeight()) / 4 + metrics.getAscent();

			graphics.setFont(font);

			// color for text
			graphics.setColor(Color.BLACK);

			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// draw string at center of image
			graphics.drawString(text, positionX, positionY);

			baos = new ByteArrayOutputStream();

			ImageIO.write(bufferedImage, "png", baos);

			byte[] bytes = baos.toByteArray();

			is = new ByteArrayInputStream(bytes);

			image = ImageIO.read(is);
			return image;

		} catch (IOException e) {

			Logger.e(TAG, e.getMessage());

		} finally {
			if (bufferedImage != null) {
				bufferedImage.flush();
				bufferedImage = null;
			}

			if (baos != null) {
				try {
					baos.flush();
					baos.close();
				} catch (IOException e) {
					Logger.e(TAG, e.getMessage());
				}

				baos = null;
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Logger.e(TAG, e.getMessage());
				}
				is = null;
			}
		}

		return image;
	}
}
