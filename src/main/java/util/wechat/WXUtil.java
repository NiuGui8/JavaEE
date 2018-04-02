/**
*���ܻ�ȡ΢��unionid
*/
class  WXGetUnionID
{
	private String wxUsername; //�û���
	private String wxPassword; //����
	private String openid;
	private String session_key;//�Ự��Կ
	private String encryptedData;//��������
	private String iv;//��ʼ����
	
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
	
	/**
	 * ΢��С�����¼����openid
	 * */
	public String wxLogin() {
		logger.info("΢��С�����¼  username��+ " + wxUsername + "password: " +wxPassword);
		User saltUser = loginService.queryUserByName(wxUsername);
		if(saltUser == null) {
			logger.info("û�и��û���");
			actionResult = ActionResult.getActionResult(new OpAttributes("loginUser",null));
			return returnType();
		}
		wxPassword = (IDGenerator.encryptEncode((wxPassword + saltUser.getSalt()), GlobaConstants.ENCRYPTION_ALGORITHM));
		User loginUser = userService.wxLogin(wxUsername,wxPassword);
		if(loginUser == null) {
			logger.info("�������");
			actionResult = ActionResult.getActionResult(new OpAttributes("loginUser",null));
			return returnType();
		}
		Boolean isSuccess = false;
		if("".equals(openid) || openid == null) {
			isSuccess = true;
		}else {
			isSuccess = userService.bindWXOpenid(wxUsername,openid);
		}
		if(isSuccess) {
			logger.info("��΢��С����openid�ɹ�");
			List<String> permissionList = loginService.getPermissionByShiro(wxUsername);
			if(permissionList != null) {
				actionResult = ActionResult.getActionResult(new OpAttributes("loginUser",loginUser).add("permissionList", permissionList));
			}else {
				logger.info("��ȡ�û�Ȩ��ʧ��");
			}
		}else {
			logger.info("��΢��С����openidʧ��");
			actionResult = ActionResult.getActionResult(new OpAttributes("loginUser",null));
			return returnType();
		}
		return returnType();
	}
	
	private String stringArray;

	public String getStringArray() {
		return stringArray;
	}

	public void setStringArray(String stringArray) {
		this.stringArray = stringArray;
	}

	/**
	 * ����unionid
	 * @throws UnsupportedEncodingException 
	 * */
	public String unlockUnionid() throws UnsupportedEncodingException {
		logger.info("����unionid");
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
		// �����ܵ�����
        byte[] dataByte = Base64.decode(encryptedData2);
        // ������Կ
        byte[] keyByte = Base64.decode(session_key2);
        // ƫ����
        byte[] ivByte = Base64.decode(iv2);
        
        try {
               // �����Կ����16λ����ô�Ͳ���.  ���if �е����ݺ���Ҫ
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // ��ʼ��
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// ��ʼ��
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
