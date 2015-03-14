package com.aggrepoint.winlet.elasticsearch;

import java.util.StringTokenizer;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * 提供TransportClient对象
 * 
 * @author Jim
 */
public class TransportClientFactory {
	String nodes;
	TransportClient client;

	public TransportClientFactory() {

	}

	public void setNodes(String nodes) {
		this.nodes = nodes;
	}

	public TransportClient getClient() {
		if (client == null) {
			client = new TransportClient();

			StringTokenizer st = new StringTokenizer(nodes, ",; ");
			while (st.hasMoreElements()) {
				String node = st.nextToken();
				int idx = node.indexOf(":");
				String host;
				int port = 9300;
				if (idx > 0) {
					host = node.substring(0, idx);
					port = Integer.parseInt(node.substring(idx + 1));
				} else {
					host = node;
				}

				client.addTransportAddress(new InetSocketTransportAddress(host,
						port));
			}
		}

		return client;
	}
}
