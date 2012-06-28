package org.vaadin.addons.logview.table;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.vaadin.data.Container;

public class DebugContainer implements InvocationHandler {
	private final Container target;

	private final Container proxy = (Container)Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {
		Container.class, Container.Indexed.class
	}, this);

	private final Set<String> allowed = ImmutableSet.of( //
		"int size", //
		"boolean containsId", //
		"java.lang.Class getType", //
		"java.lang.Object getIdByIndex", //
		"java.util.Collection getContainerPropertyIds", //
		"com.vaadin.data.Property getContainerProperty" //
	);

	public DebugContainer(Container target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		StringBuffer sb = new StringBuffer();
		sb.append(method.getReturnType().getName());
		sb.append(' ');
		sb.append(method.getName());
		if(!allowed.contains(sb.toString())) {
			sb.append('(');
			if(args != null) {
				for(int i = 0, to = args.length; i < to; i++) {
					if(i != 0) {
						sb.append(", ");
					}
					if(args[i] == null) {
						sb.append("null");
					} else if(args[i] instanceof String) {
						sb.append('"');
						sb.append(args[i]);
						sb.append('"');
					} else {
						sb.append(args[i].toString());
					}
				}
			}
			sb.append(')');
			System.err.println(sb.toString());
		}
		return method.invoke(target, args);
	}

	public Container getProxy() {
		return proxy;
	}
}
