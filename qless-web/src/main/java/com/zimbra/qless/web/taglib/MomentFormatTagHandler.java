package com.zimbra.qless.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

@SuppressWarnings("serial")
public class MomentFormatTagHandler extends TagSupport {
	String value, format;
	boolean unix;
	
	@Override
	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			out.print("<script>");
			out.print("document.write(");
			
			if (unix && value != null) {
				out.print("moment.unix(" + value + ")");
			} else {
				out.print("moment(" + (value == null ? "" : value) + ")");
			}
			
			out.print(".format('" + (format == null ? "lll" : format) + "'");
			
			out.print(")");
			out.print("</script>");
			return SKIP_BODY;
		} catch (IOException e) {
			throw new JspException(e);
		}
	}
	
	public String getFormat() {
		return format;
	}
	
	public boolean getUnix() {
		return unix;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public void setUnix(boolean unix) {
		this.unix = unix;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
