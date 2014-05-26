package com.test.amazon;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-context.xml" })
public class AmazonFileTest {

	@Test
	public void parse() {
		try {
			URL url = new URL("http://vblrimpsmstp-01:9080/corp/L001/consumer/images/backgrounds/select.gif");
			BufferedImage image = null;
			image = ImageIO.read(url);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg",bos);
			image = ImageIO.read(url);
			ImageIO.write(image, "jpg",new File("D:\\temp\\OutOfMemoryError.jpg"));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
