/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.pki

import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameEdiPartyType
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity

class NscsPkiUtilsTest extends CdiSpecification {

    def "#convertSubjectAltNameFieldTypeToNscsFormat return a not valid SubjectAltNameFormat" () {
        given:
        SubjectAltNameFieldType subjectAltNameFieldType = SubjectAltNameFieldType.IP_ADDRESS
        BaseSubjectAltNameDataType subjectAltNameDataType = new SubjectAltNameEdiPartyType()
        subjectAltNameDataType.setNameAssigner("This is name")
        subjectAltNameDataType.setPartyName("This is name")
        when:
        SubjectAltNameFormat subjectAltNameFormat = NscsPkiUtils.convertSubjectAltNameFieldTypeToNscsFormat(subjectAltNameFieldType, subjectAltNameDataType)
        then:
        subjectAltNameFormat == SubjectAltNameFormat.NONE
    }

    def "create entity with valid #subjectaltname #subjectaltnametype" () {
        given:
        String subject="CN=5G139vDURI001"
        when:
        Entity entity=NscsPkiUtils.createEntity(subject,subjectaltname,subjectaltnametype)
        then:
        entity.getEntityInfo().getSubjectAltName().getSubjectAltNameFields().value.get(0).toString() == subjectaltname
        where:
        subjectaltname << [
                "5G139vDURI001",
                "user@5G139vDURI001.ie"
        ]
        subjectaltnametype << [
                "DNS_NAME",
                "RFC822_NAME"
        ]
    }
    def "#convertSubjectAltNameFieldTypeToNscsFormat return a valid SubjectAltNameFormat RFC822_NAME" () {
        given:
        SubjectAltNameFieldType subjectAltNameFieldType = SubjectAltNameFieldType.RFC822_NAME
        BaseSubjectAltNameDataType subjectAltNameDataType = new SubjectAltNameEdiPartyType()
        subjectAltNameDataType.setNameAssigner("This is name")
        subjectAltNameDataType.setPartyName("This is name")
        when:
        SubjectAltNameFormat subjectAltNameFormat = NscsPkiUtils.convertSubjectAltNameFieldTypeToNscsFormat(subjectAltNameFieldType, subjectAltNameDataType)
        SubjectAltNameFieldType subjectAltNameValueType = NscsPkiUtils.convertSubjectAltNameFormatToPkiFormat(subjectAltNameFormat)
        then:
        subjectAltNameFormat == SubjectAltNameFormat.RFC822_NAME
        subjectAltNameValueType.name() ==  SubjectAltNameFieldType.RFC822_NAME.name()

    }
    def "#convertSubjectAltNameFieldTypeToNscsFormat return a valid SubjectAltNameFormat DNS_NAME" () {
        given:
        SubjectAltNameFieldType subjectAltNameFieldType = SubjectAltNameFieldType.DNS_NAME
        BaseSubjectAltNameDataType subjectAltNameDataType = new SubjectAltNameEdiPartyType()
        subjectAltNameDataType.setNameAssigner("This is name")
        subjectAltNameDataType.setPartyName("This is name")
        when:
        SubjectAltNameFormat subjectAltNameFormat = NscsPkiUtils.convertSubjectAltNameFieldTypeToNscsFormat(subjectAltNameFieldType, subjectAltNameDataType)
        SubjectAltNameFieldType subjectAltNameValueType = NscsPkiUtils.convertSubjectAltNameFormatToPkiFormat(subjectAltNameFormat)
        then:
        subjectAltNameFormat == SubjectAltNameFormat.FQDN
        subjectAltNameValueType.name() ==  SubjectAltNameFieldType.DNS_NAME.name()

    }
}
