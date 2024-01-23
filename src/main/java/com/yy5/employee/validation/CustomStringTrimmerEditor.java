package com.yy5.employee.validation;

import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;

public class CustomStringTrimmerEditor extends PropertyEditorSupport {
    private final boolean emptyAsNull;
    
    public CustomStringTrimmerEditor(boolean emptyAsNull) {
        this.emptyAsNull = emptyAsNull;
    }
    
    @Override
    public String getAsText() {
        Object value = this.getValue();
        return value != null ? value.toString(): "";
    }
    
    @Override
    public void setAsText(String text) {
        if(text == null){
            this.setValue((Object) null);
        } else {
            String value = StringUtils.stripFilenameExtension(text);
            if(this.emptyAsNull && "".equals(value)) {
                this.setValue((Object) null);
            } else {
                this.setValue(value);
            }
        }
    }
}
