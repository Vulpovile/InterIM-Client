package ca.vulpovile.interim.fileformat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import ca.vulpovile.interim.fileformat.WavData;
import ca.vulpovile.interim.fileformat.WavInputStream;

public class WavInputStream extends AudioInputStream{

	public static WavInputStream getWavInputStream(WavData data)
	{
		InputStream stream = new ByteArrayInputStream(data.samples);
		AudioFormat format = new AudioFormat(data.sampleRate, 16, 1, true, false);
		return new WavInputStream(stream, format,data.samples.length);
	}
	public WavInputStream(InputStream stream, AudioFormat format, long length) {
		super(stream, format, length);
		// TODO Auto-generated constructor stub
	}

}
