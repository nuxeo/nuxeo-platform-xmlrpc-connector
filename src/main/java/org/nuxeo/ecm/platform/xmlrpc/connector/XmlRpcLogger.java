package org.nuxeo.ecm.platform.xmlrpc.connector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcRequest;
import org.nuxeo.runtime.api.Framework;

/**
 * Class used to the xmlrpc calls of the user
 *
 */
public class XmlRpcLogger {

	private static final String NUXEO_DOMAIN = "Nuxeo-Domain";

	public static final String KEY_XMLRPC_BLOB_DIRECTORY = "xmlrpc.blob.directory";

	public static final String KEY_XMLRPC_DUMB_BLOBS = "xmlrpc.dump.blobs";

	private static final Log log = LogFactory.getLog(XmlRpcLogger.class);

	static boolean xmlrpcDumpBlobs = false;

	static File xmlrpcBlobDirectory;



	static  {
		String tmp = Framework.getProperty(KEY_XMLRPC_BLOB_DIRECTORY);
		if ( tmp != null ) {
			xmlrpcBlobDirectory = new File(tmp);
		} else  {
			xmlrpcBlobDirectory = new File( System.getProperty("java.io.tmpdir"));
		}
		tmp = Framework.getProperty(KEY_XMLRPC_DUMB_BLOBS);
		if ( tmp != null && ("1".equals(tmp.trim()) || "true".equals(tmp.trim().toLowerCase())) ) {
			xmlrpcDumpBlobs = true;
		}

	}

	public static void log(HttpServletRequest httpRequest, XmlRpcRequest xmlRpcRequest, Object resultObject){
		String sid = httpRequest.getSession().getId();

		String url = httpRequest.getRequestURL().toString();

		String domain = httpRequest.getHeader(NUXEO_DOMAIN);

		String user = "N/A";
		Principal userPrincipal = httpRequest.getUserPrincipal();
		if ( userPrincipal != null ){
			user = userPrincipal.getName();
		}

		String method = xmlRpcRequest.getMethodName();
		method = method.substring(method.lastIndexOf('.')+1);

		String parameters = parameters(xmlRpcRequest);

		String result = resultObject.toString();
		String logMsg = String.format("sid: %s; user: %s; url: %s; domain: %s; method: %s; params: %s; result: %s", sid, user, url, domain, method, parameters, result);
		log.debug(logMsg);

	}

	private static String parameters(XmlRpcRequest xmlRpcRequest) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		int n = xmlRpcRequest.getParameterCount();
		if ( n > 0 ) {
			sb.append(toString(xmlRpcRequest.getParameter(0)));
		}
		if ( n > 1 ) {
			for ( int i = 1 ; i < n ; i++) {
				sb.append("|");
				sb.append(toString(xmlRpcRequest.getParameter(i)));
			}
		}
		sb.append("]");
		return sb.toString();
	}

	private static Object toString(Object parameter) {
		if ( parameter instanceof Object[]) {
			return Arrays.toString((Object[])parameter);
		}
		if ( parameter instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) parameter;
			for (Entry<String, Object> entry : map.entrySet()){
				if ( entry.getValue() instanceof byte[] && xmlrpcDumpBlobs ){
					byte[] content = (byte[]) entry.getValue();
					File file = saveInFile(content);
					map.put(entry.getKey(), file.toURI().toString());
				}
			}
		}
		return parameter.toString();
	}

	private static File saveInFile(byte[] content) {
		if ( !xmlrpcBlobDirectory.exists() ) {
			xmlrpcBlobDirectory.mkdirs();
		}
		FileOutputStream fos = null;
		try {
			File file = File.createTempFile("file_", ".dump", xmlrpcBlobDirectory);
			fos = new FileOutputStream(file);
			fos.write(content);
			return file;
		} catch (IOException e) {
			log.debug(e);
			return null;
		} finally {
			if ( fos != null) {
				try { fos.close(); } catch (IOException e) { }
			}
		}
	}

}
