package com.ruoyi.ym.utils;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public final class QrCodeUtils
{
    private static final int QR_SIZE = 300;

    private static final int H_PADDING = 20;

    private static final int TEXT_AREA_PADDING = 12;

    private static final int FONT_SIZE = 18;

    private QrCodeUtils()
    {
    }

    public static byte[] generatePngBytes(String content) throws Exception
    {
        Map<EncodeHintType, Object> hints = buildHints();
        BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return out.toByteArray();
    }

    /**
     * 生成组合图：上方二维码，下方「查询码：xxx」
     */
    public static byte[] generateLabeledQrPngBytes(String scanUrl, String verificationCode) throws Exception
    {
        Map<EncodeHintType, Object> hints = buildHints();
        BitMatrix matrix = new QRCodeWriter().encode(scanUrl, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);

        String label = "查询码：" + (verificationCode == null ? "" : verificationCode.trim());
        java.awt.Font font = QrLabelFontLoader.getFont(FONT_SIZE);
        BufferedImage measureImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D measureG = measureImg.createGraphics();
        measureG.setFont(font);
        FontMetrics fm = measureG.getFontMetrics();
        int textWidth = fm.stringWidth(label);
        int textHeight = fm.getHeight();
        measureG.dispose();

        int canvasWidth = Math.max(QR_SIZE, textWidth + H_PADDING * 2);
        int canvasHeight = QR_SIZE + TEXT_AREA_PADDING + textHeight + TEXT_AREA_PADDING;

        BufferedImage canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        int qrX = (canvasWidth - QR_SIZE) / 2;
        g.drawImage(qrImage, qrX, 0, null);
        g.setColor(Color.BLACK);
        g.setFont(font);
        int textX = (canvasWidth - textWidth) / 2;
        int textY = QR_SIZE + TEXT_AREA_PADDING + fm.getAscent();
        g.drawString(label, textX, textY);
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(canvas, "PNG", out);
        return out.toByteArray();
    }

    private static Map<EncodeHintType, Object> buildHints()
    {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);
        return hints;
    }
}
