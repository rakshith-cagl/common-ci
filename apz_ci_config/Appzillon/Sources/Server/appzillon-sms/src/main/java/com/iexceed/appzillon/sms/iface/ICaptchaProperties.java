package com.iexceed.appzillon.sms.iface;

import nl.captcha.audio.producer.VoiceProducer;
import nl.captcha.backgrounds.BackgroundProducer;
import nl.captcha.gimpy.GimpyRenderer;
import nl.captcha.text.producer.TextProducer;
import nl.captcha.text.renderer.WordRenderer;

import javax.sound.sampled.AudioFileFormat;
import java.awt.*;
import java.util.List;

public interface ICaptchaProperties {

    int length();

    int width();

    int height();

    CaptchaType captchaType();

    AudioFileFormat.Type audioFileFormat();

    ImageType imageType();

    TextProducer textProducer(CaptchaType type);

    VoiceProducer voiceProducer();

    BackgroundProducer backgroundProducer();

    BackgroundProducer backgroundProducer(Color colors, Color color);

    WordRenderer wordrenderer();

    WordRenderer wordrenderer(List<Color> colors, List<Font> fonts);

    GimpyRenderer gimpyRenderer();

    nl.captcha.noise.NoiseProducer noiseProducer();

    nl.captcha.audio.noise.NoiseProducer audioNoiseProducer();

    List<Color> textColors();

    List<Font> fonts();

    enum CaptchaType {
        ALPHA("APLHA"), NUMERIC("NUMERIC"), ALPHANUMERIC("APLHANUMERIC"), DUMMY("DUMMY");
        final String typeOfCaptcha;

        private CaptchaType(String typeOfCaptcha) {
            this.typeOfCaptcha = typeOfCaptcha;
        }

        @Override
        public String toString() {
            return typeOfCaptcha;
        }
    }

    enum ImageType {
        GIF("GIF"), PNG("PNG"), JPG("JPG");
        String imgType;

        private ImageType(String imgType) {
            this.imgType = imgType;
        }

        @Override
        public String toString() {
            return imgType;
        }
    }
}
