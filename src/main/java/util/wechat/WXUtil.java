/**
*解密获取微信unionid
*/
class  WXGetUnionID
{
	private String wxUsername; //用户名
	private String wxPassword; //密码
	private String openid;
	private String session_key;//会话密钥
	private String encryptedData;//加密数据
	private String iv;//初始向量
	
	public String getSession_key() {
		return session_key;
	}

	public void setSession_key(String session_key) {
		this.session_key = session_key;
	}

	public String getEncryptedData() {
		return encryptedData;
	}

	public void setEncryptedData(String encryptedData) {
		this.encryptedData = encryptedData;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getWxUsername() {
		return wxUsername;
	}
	public void setWxUsername(String wxUsername) {
		this.wxUsername = wxUsername;
	}
	public String getWxPassword() {
		return wxPassword;
	}
	public void setWxPassword(String wxPassword) {
		this.wxPassword = wxPassword;
	}
	
	private String stringArray;

	public String getStringArray() {
		return stringArray;
	}

	public void setStringArray(String stringArray) {
		this.stringArray = stringArray;
	}

	/**
	 * 解密unionid
	 * @throws UnsupportedEncodingException 
	 * */
	public String unlockUnionid() throws UnsupportedEncodingException {
		logger.info("解密unionid");
		String unionid = unlockUtil(openid,new String(session_key.getBytes()),new String(iv.getBytes()),new String(encryptedData.getBytes()));
		String[] userInfo = unionid.split(",");
		StringBuffer sb = new StringBuffer();
		for (String string : userInfo) {
			if(string.contains("unionId")) {
				String[] split = string.split(":");
				sb.append(split[1]);
			}
		}
		actionResult = ActionResult.getActionResult(new OpAttributes("unionId",sb == null ? null : sb.toString()));
		return returnType();
	}

	private String unlockUtil(String openid2, String session_key2, String iv2, String encryptedData2) {
		// 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData2);
        // 加密秘钥
        byte[] keyByte = Base64.decode(session_key2);
        // 偏移量
        byte[] ivByte = Base64.decode(iv2);
        
        try {
               // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return result;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		return null;
	}
}

}
