package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.sms.iface.ICaptchaProperties;
import nl.captcha.audio.noise.NoiseProducer;
import nl.captcha.audio.noise.RandomNoiseProducer;
import nl.captcha.audio.producer.RandomNumberVoiceProducer;
import nl.captcha.audio.producer.VoiceProducer;
import nl.captcha.backgrounds.BackgroundProducer;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.BlockGimpyRenderer;
import nl.captcha.gimpy.GimpyRenderer;
import nl.captcha.noise.CurvedLineNoiseProducer;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.producer.TextProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;
import nl.captcha.text.renderer.WordRenderer;

import javax.sound.sampled.AudioFileFormat;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static com.iexceed.appzillon.utils.ServerConstants.ARIAL;
import static com.iexceed.appzillon.utils.ServerConstants.COURIER;

public class CaptchaDefaultPropertiesImpl implements ICaptchaProperties {

    private final char[] srcChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private final char[] srcNums = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public int length() {
        return 6;
    }

    public int width() {
        return 200;
    }

    public int height() {
        return 50;
    }

    public ICaptchaProperties.CaptchaType captchaType() {
        return ICaptchaProperties.CaptchaType.ALPHANUMERIC;
    }

    public ICaptchaProperties.ImageType imageType() {
        return ICaptchaProperties.ImageType.GIF;
    }

    public TextProducer textProducer(ICaptchaProperties.CaptchaType captcha) {
        if (ICaptchaProperties.CaptchaType.ALPHANUMERIC.equals(captcha))
            return new DefaultTextProducer(length(), srcChars);
        if (ICaptchaProperties.CaptchaType.NUMERIC.equals(captcha))
            return new DefaultTextProducer(length(), srcNums);
        return new DefaultTextProducer();
    }

    public BackgroundProducer backgroundProducer() {
        return new GradiatedBackgroundProducer();
    }

    public BackgroundProducer backgroundProducer(Color colors, Color color) {
        return new GradiatedBackgroundProducer(colors, color);
    }

    public WordRenderer wordrenderer() {
        return new DefaultWordRenderer();
    }

    public WordRenderer wordrenderer(java.util.List<Color> colors, java.util.List<Font> fonts) {
        return new DefaultWordRenderer(colors, fonts);
    }

    public GimpyRenderer gimpyRenderer() {
        return new BlockGimpyRenderer(20);
    }

    public VoiceProducer voiceProducer() {
        return new RandomNumberVoiceProducer();
    }

    public nl.captcha.noise.NoiseProducer noiseProducer() {
        return new CurvedLineNoiseProducer(Color.WHITE, 4.0F);
    }

    public java.util.List<Color> textColors() {
        java.util.List<Color> colors = new ArrayList<>();
        colors.add(Color.BLACK);
        colors.add(Color.BLUE);
        return colors;
    }

    public java.util.List<Font> fonts() {
        return Arrays.asList(new Font(ARIAL, Font.ITALIC, 30), new Font(COURIER, Font.ITALIC, 30), new Font(ARIAL, Font.BOLD, 30), new Font(COURIER, Font.BOLD, 30), new Font(ARIAL, Font.PLAIN, 30), new Font(COURIER, Font.PLAIN, 30));
    }

    public NoiseProducer audioNoiseProducer() {
        return new RandomNoiseProducer();
    }

    public AudioFileFormat.Type audioFileFormat() {
        return AudioFileFormat.Type.WAVE;
    }
}

