/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.api.iscf;

import java.io.Serializable;

/**
 *
 * @author enmadmin
 */
public class SubjectAltNameParam implements Serializable {
    
    private static final long serialVersionUID = 7680909173303601535L;
    
    protected SubjectAltNameFormat subjectAltNameFormat;
    
    protected BaseSubjectAltNameDataType subjectAltNameData;
    
    public SubjectAltNameParam(SubjectAltNameFormat format, BaseSubjectAltNameDataType data) {
        subjectAltNameFormat = format;
        subjectAltNameData = data;
    }
    
    public void setSubjectAltNameFormat(SubjectAltNameFormat format) {
        subjectAltNameFormat = format;
    }
    
    public void setSubjectAltNameData(BaseSubjectAltNameDataType data) {
        subjectAltNameData = data;
    }
    
    public SubjectAltNameFormat getSubjectAltNameFormat() {
        return subjectAltNameFormat;
    }
    
    public BaseSubjectAltNameDataType getSubjectAltNameData() {
        return subjectAltNameData;
    }
    
}
