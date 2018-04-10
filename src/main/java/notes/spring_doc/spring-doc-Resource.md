# Resource

## ���
	��׼��java.util.URL ���䴦��ʽ�����ܺܺõĸ���URL������ײ���Դ�����磺

+ ����ͨ����·�������������Դ
+ �������ServletContext 

	Spring �� Resource�ӿ��ܸ��õĴ���ײ���Դ:

	public interface Resource extends InputStreamSource {
		boolean exists();
		boolean isOpen();
		URL getURL() throws IOException;
		File getFile() throws IOException;
		Resource createRelative(String relativePath) throws IOException;
		String getFilename();
		String getDescription();
	}

	public interface InputStreamSource {
		InputStream getInputStream() throws IOException;
	}

## Resource ����Ҫ�ļ�������

+ getInputStream()
+ exists()
+ isOpen()
+ getDescription()

### getInputStream()
	��λ�ʹ���Դ������һ���ɹ�����Դ�ĵ�������InputStream,��Ҫ��ʱ�ر�����

### exists()
	�ж���Դ�Ƿ�ȷʵ����

### isOpen()
	�����Դ�Ƿ����һ�����п������ľ�������Ϊtrue��inputSream���ܱ���ζ�ȡ����ȡ��һ�ξ�����ر�������ֹ��Դ�ڴ�й©��

### getDescription()
	������Դ��������Ϣ����ͨ������ȫ�޶����ļ�������Դ��ʵ��URL��

## Resourceʵ����
### UrlResource
	UrlResource��������java.util.URL,�������������κ�ͨ��ͨ��URL���ʵĶ������磺�ļ���httpĿ�꣬ftpĿ��ȣ�
����URL���б�׼���ַ���ͨ����Щ��׼����ǰ׺�����Ա�ʶ��Щ��Դ�����͡����磺

+ file�� ͨ���ļ�ϵͳ��������Դ 
+ http: ͨ��httpЭ�������Դ
+ ftp: ͨ��FTPͨ����������Դ�ȵ�

PropertyEditor ���Ծ��������ĸ�ʵ����,����Դ�ַ�����ǰ׺Ϊ��֪������ʱ�����Դ������ʺϵ�ʵ���࣬���޷�ʶ��ʱ��Ĭ��Ϊ��ͨ��URL�ַ�����������UrlResource��

### ClassPathResource
	����·���л�ȡ��Դ�����ǲ��ܻ�ȡjar��û����չ����·������Դ����ʹ���߳�������������������������������������Դ�ĸ����ࡣ
����classpath:ǰ׺�ַ���ʱ���ô�ʵ�֡�

### FileSystemResource
	This is a Resource implementation for java.io.File handles. It obviously supports resolution as a File, and as a URL.

### ServletContextResource
	��������ServletContext��ص���Դ�������webӦ�ó���ĸ�Ŀ¼�н������·����֧��URL����������

### InputStreamResource
	Ϊ����InputStream��ʵ�֣���û���������ʵ�ʵ�������ʱ���ô��࣬�������Ҫ����Դ������������ĳ���ط�����������Ҫ��ζ�ȡһ��������Ҫʹ������

### ByteArrayResource
	Ϊ�������ֽ������ʵ�֣�Ϊ�������ֽ����鴴����һ��ByteArrayInputStream �������ڴ��κθ������ֽ������м������ݷǳ����ã�������Ҫʹ�õ�һ��InputStreamResource��

## ResourceLoader
### ˵��
	ResourceLoader��ʵ���࣬���Է���һ����Դʵ�壺

	public interface ResourceLoader {

		Resource getResource(String location);

	}

### ע��
	���е�ApplicationContext��ʵ���� ResourceLoader �ӿڣ�������е�ApplicationContext����������ȡResource��ʵ����

	Resource template = ctx.getResource("some/resource/path/myTemplate.txt");

��������ض��� ApplicationContext �е��� `getResource()` �����Ҵ����·��û���ر��ǰ׺���������ض��� ApplicationContext �õ��ض��� Resource��ʵ����
��ˣ����������ʺ����ض�Ӧ�ó��������ĵķ�ʽ������Դ��

�������ǿ��ʹ��ĳһ��ʵ���࣬������·���м���ǰ׺���磺��ǿ��ʹ�� `ClassPathResource`���ͼ��� classpath:

	Resource template = ctx.getResource("classpath:some/resource/path/myTemplate.txt");


<table>
  <tr>
    <th width=330%>ǰ׺</th>
    <th width=33%,>����</th>
    <th width="34%">˵��</th>
  </tr>
  <tr>
    <td> classpath: </td>
    <td> classpath:com/myapp/config.xml  </td>
    <td> ����·������  </td>
  </tr>
  <tr>
    <td>file: </td>
    <td> file:///data/config.xml </td>
    <td> ���ļ�ϵͳ���أ�����ΪURL </td>
  <tr>
    <td>http: </td>
    <td> http://myserver/logo.png </td>
    <td>  ��http���أ�����Ϊһ��URL </td>
  </tr>
   <tr>
    <td>(none) </td>
    <td> /data/config.xml </td>
    <td>  ���� ApplicationContext���� </td>
  </tr>
</table>
