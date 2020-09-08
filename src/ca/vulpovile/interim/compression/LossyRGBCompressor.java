package ca.vulpovile.interim.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LossyRGBCompressor {
	public static byte[] compress(short[][][] compressable, byte compressionLevel)
	{
		try{
			int origLength = compressable.length * compressable[0].length * compressable[0][0].length;
			compressable = shrink(compressable, compressionLevel);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeInt(compressable.length);
			dos.writeInt(compressable[0].length);
			dos.writeInt(compressable[0][0].length);
			for(int x = 0; x < compressable.length; x++)
			{
				for(int y = 0; y < compressable[x].length; y++)
				{
					for(int c = 0; c < compressable[x][y].length; c++)
					{
						baos.write((byte)compressable[x][y][c]);
					}
				}
			}
			dos.writeByte(compressionLevel);
			dos.close();
			baos.close();
			byte[] res = baos.toByteArray();
			System.out.println("Size after BMP Compression : " + ((float)res.length)/(float)origLength + "x the size");
			return res;
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	public static short[][][] decompress(byte[] compressed)
	{
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
			DataInputStream dis = new DataInputStream(bais);
			short[][][] rgb = new short[dis.readInt()][dis.readInt()][dis.readInt()];
			for(int x = 0; x < rgb.length; x++)
			{
				for(int y = 0; y < rgb[x].length; y++)
				{
					for(int c = 0; c < rgb[x][y].length; c++)
					{
						rgb[x][y][c] = (short) (dis.readByte() & 0xFF);
					}
				}
			}
			rgb = expand(rgb, dis.readByte());
			dis.close();
			bais.close();
			return rgb;
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	private static short[][][] shrink(short[][][] rgb, byte factor)
	{
		short[][][] shrunkrgb = new short[rgb.length/factor][rgb[0].length/factor][3];
		for(int x = 0; x < shrunkrgb.length; x++)
		{
			for(int y = 0; y < shrunkrgb[0].length; y++)
			{
				for(int c = 0; c < 3; c++)
				{
					for(int sectx = 0; sectx < factor; sectx++)
					{
						for(int secty = 0; secty < factor; secty++)
						{
							shrunkrgb[x][y][c] += rgb[x*factor+sectx][y*factor+secty][c];
						}
					}
					shrunkrgb[x][y][c] /= factor*factor;
					
				}
			}
		}
		return shrunkrgb;
	}
	
	private static short[][][] expand(short[][][] rgb, int factor)
	{
		short[][][] expandedrgb = new short[rgb.length*factor][rgb[0].length*factor][3];
		for(int x = 0; x < expandedrgb.length; x++)
		{
			for(int y = 0; y < expandedrgb[0].length; y++)
			{
				for(int c = 0; c < 3; c++)
				{
					expandedrgb[x][y][c] = rgb[x/factor][y/factor][c];
				}
			}
		}
		return expandedrgb;
	}
}
