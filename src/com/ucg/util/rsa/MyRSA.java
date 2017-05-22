package com.ucg.util.rsa;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

public class MyRSA {

	public static final String KEY_ALGORITHM = "RSA";

	public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
	public static final String PUBLIC_KEY = "publicKey";
	public static final String PRIVATE_KEY = "privateKey";

	/** RSA密钥长度必须是64的倍数，在512~65536之间。默认是1024 */
	public static final int KEY_SIZE = 2048;

	public static final String PLAIN_TEXT = "123456";

	// 以下是一对RSA公私钥，result是一串用公钥加密后的密文
	public static String private_key_string = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL8sg7akYXOPKFFI"+
												"CYonL3H1lc2WsFKbpG3tb6bbaXBoY0TEJd6Xzju/F/8M4LEvrrMuL8WQnSmyX8J0"+
												"PHJeet4p58qudNcHn810IjlPVv4PO8BmUYWYB7FFG8ogJY8vwZ4DupTzsEHP3H2F"+
												"rFL0EtibJUVTcxeRF1SGJ77xSczlAgMBAAECgYEAl3kW16VBTcW1RUnVvTA1KI9U"+
												"5bOq+5k03KDP4Z5h2d3sIbVk6AzSLIbJ5Z13fwzv2CsVUO23iS07MKDWsCvqHzQq"+
												"Cu2Wtd7p7//gBKP8jx6+2l12IjuuAAAnFB1tz/U6uNXT8M8WxLx1YQxD4OBH2XZO"+
												"KBnXH9fK9y1Y+pvHdDUCQQDslm7smDANdLMI1H4ZoR9Srmex1B7XuBes+GrX1BkM"+
												"pPZL06LpcoQGrCz/GQh656WNShU79fbOcm2uhzSB3CzDAkEAztwoOc+AbnF9PKks"+
												"3vJIH6PqXX4eI2DK3m4ATbeHvfHtMbGjoheVgvmiKcxPgY0UtEy7ywnHBmDaZb5O"+
												"7SwlNwJBANXGsIBUm8bs1gF6kF70Oetp8AS9WQkvLSV8an6PBfto66xklWY/tZCZ"+
												"7yaqJgF4Yx0h/cHqZJLyzqzPOuDEZCUCQD+7Bk2hR0E/h8ULnf5mCKBu4MLDG0ft"+
												"BzN2EnPfKlvz0A7yWqaSu4ZpoHNeNdSFAa72wRixv3aQmlikRC6/3gsCQHhX94mQ"+
												"fmgo/E0UplC97Z1KTcwNauSJUqQ2JbVTGwQDZFiGuieZTCQtJINeg4u/QfBm8FaZ"+
												"XWAq03HiCkUKcc4=";

	public static String public_key_string = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/LIO2pGFzjyhRSAmKJy9x9ZXN"+
												"lrBSm6Rt7W+m22lwaGNExCXel847vxf/DOCxL66zLi/FkJ0psl/CdDxyXnreKefK"+
												"rnTXB5/NdCI5T1b+DzvAZlGFmAexRRvKICWPL8GeA7qU87BBz9x9haxS9BLYmyVF"+
												"U3MXkRdUhie+8UnM5QIDAQAB";

	
	public static void main(String[] args) throws Exception {
		
		PublicKey pubKey=restorePublicKey(Base64Utils.decode(public_key_string));
		PrivateKey priKey=restorePrivateKey(Base64Utils.decode(private_key_string));
		
		//1.用公钥加密
		byte[] encodeBytes=RSAEncode(pubKey, PLAIN_TEXT.getBytes());
		System.out.println("-----加密结果----\n"+Base64Utils.encode(encodeBytes));
		
		//1.用私钥解密
		String decodeResult=RSADecode(priKey, encodeBytes);
		System.out.println("-----解密结果----\n"+decodeResult);
		
	

	}

	/**
	 * 生成密钥对。注意这里是生成密钥对KeyPair，再由密钥对获取公私钥
	 *
	 * @return
	 */
	public static Map<String, byte[]> generateKeyBytes() {

		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			keyPairGenerator.initialize(KEY_SIZE);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

			Map<String, byte[]> keyMap = new HashMap<String, byte[]>();
			keyMap.put(PUBLIC_KEY, publicKey.getEncoded());
			keyMap.put(PRIVATE_KEY, privateKey.getEncoded());
			return keyMap;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 还原公钥，X509EncodedKeySpec 用于构建公钥的规范
	 *
	 * @param keyBytes
	 * @return
	 */
	public static PublicKey restorePublicKey(byte[] keyBytes) {
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);

		try {
			KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
			PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
			return publicKey;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 还原私钥，PKCS8EncodedKeySpec 用于构建私钥的规范
	 *
	 * @param keyBytes
	 * @return
	 */
	public static PrivateKey restorePrivateKey(byte[] keyBytes) {
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
		try {
			KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
			PrivateKey privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
			return privateKey;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加密，三步走。
	 *
	 * @param key
	 * @param plainText
	 * @return
	 */
	public static byte[] RSAEncode(PublicKey key, byte[] plainText) {

		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(plainText);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 
	 *
	 * @param key
	 * @param encodedText
	 * @return
	 */
	public static String RSADecode(PrivateKey key, byte[] encodedText) {

		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(encodedText));
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 解密
	 * 
	 * @param cipherText
	 *            密文
	 * @param privateKey
	 *            私钥
	 * @throws Exception
	 *             异常
	 */
	public static String decryptByPrivateKey(String privateKey, String cipherText) {

		
		try {
		    byte[] keyBytes = Base64Utils.decode(privateKey);
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);

			return RSADecode(privateK, Base64Utils.decode(cipherText));

		} catch (Exception e) {
			e.printStackTrace();

			return "";

		}
	}
}