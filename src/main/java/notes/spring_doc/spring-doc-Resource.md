# ���
	��׼��java.util.URL ���䴦��ʽ�����ܺܺõĸ���URL������ײ���Դ�����磺
		*����ͨ����·�������������Դ
		*�������ServletContext 
# Resource
	Spring �� Resource�ӿ��ܸ��õĴ���ײ���Դ .

	`public interface Resource extends InputStreamSource {

    boolean exists();

    boolean isOpen();

    URL getURL() throws IOException;

    File getFile() throws IOException;

    Resource createRelative(String relativePath) throws IOException;

    String getFilename();

    String getDescription();

}`

`public interface InputStreamSource {

    InputStream getInputStream() throws IOException;

}`