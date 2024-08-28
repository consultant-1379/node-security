/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.test.pki;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.pki.common.model.EntityStatus;
import com.ericsson.oss.itpf.security.pki.common.model.Subject;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.manager.exception.configuration.algorithm.AlgorithmNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.crl.CRLExtensionException;
import com.ericsson.oss.itpf.security.pki.manager.exception.crl.CRLGenerationException;
import com.ericsson.oss.itpf.security.pki.manager.exception.crl.InvalidCRLGenerationInfoException;
import com.ericsson.oss.itpf.security.pki.manager.exception.crl.UnsupportedCRLVersionException;
import com.ericsson.oss.itpf.security.pki.manager.exception.enrollment.EnrollmentURLNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityAlreadyDeletedException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityAlreadyExistsException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityInUseException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.InvalidEntityAttributeException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.InvalidEntityException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.caentity.CANotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.category.EntityCategoryNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.category.InvalidEntityCategoryException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.endentity.OTPException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.endentity.otp.InvalidOTPCountException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.endentity.otp.InvalidOTPException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.endentity.otp.InvalidOtpValidityPeriodException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.endentity.otp.OTPExpiredException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.endentity.otp.OTPNotSetException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.InvalidProfileException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.ProfileNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.CertificateNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.InvalidSubjectException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.MissingMandatoryFieldException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.SerialNumberNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.certificateextension.InvalidSubjectAltNameExtension;
import com.ericsson.oss.itpf.security.pki.manager.exception.trustdistributionpoint.TrustDistributionPointURLNotDefinedException;
import com.ericsson.oss.itpf.security.pki.manager.exception.trustdistributionpoint.TrustDistributionPointURLNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentType;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityEnrollmentInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.TDPSUrlInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.TreeNode;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.AbstractEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entities;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.custom.EntityManagementCustomService;
import com.ericsson.oss.services.security.pkimock.api.MockEntityManagementCustomService;
import com.ericsson.oss.services.security.pkimock.api.MockEntityManagementService;

/**
 *
 * @author enmadmin
 */
@Stateless
public class TestPkiEntityManagementServiceImpl implements EntityManagementService, EntityManagementCustomService {

    @EServiceRef
    MockEntityManagementService mockEntityManagementService;

    @EServiceRef
    MockEntityManagementCustomService mockEntityManagementCustomService;

    @Override
    public <T extends AbstractEntity> T createEntity(final T t)
            throws AlgorithmNotFoundException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityServiceException,
            EntityCategoryNotFoundException, InvalidCRLGenerationInfoException, InvalidEntityException, InvalidEntityAttributeException,
            InvalidEntityCategoryException, InvalidProfileException, InvalidSubjectAltNameExtension, InvalidSubjectException,
            MissingMandatoryFieldException, ProfileNotFoundException, UnsupportedCRLVersionException {
        return mockEntityManagementService.createEntity(t);
    }

    @Override
    public void importEntities(final Entities ents) throws AlgorithmNotFoundException, CRLExtensionException, EntityAlreadyExistsException,
            EntityCategoryNotFoundException, EntityServiceException, InvalidCRLGenerationInfoException, InvalidEntityException,
            InvalidEntityAttributeException, InvalidEntityCategoryException, InvalidProfileException, InvalidSubjectAltNameExtension,
            InvalidSubjectException, MissingMandatoryFieldException, ProfileNotFoundException, UnsupportedCRLVersionException {
        mockEntityManagementService.importEntities(ents);
    }

    @Override
    public void updateEntities(final Entities ents) throws AlgorithmNotFoundException, CRLExtensionException, CRLGenerationException,
            EntityAlreadyExistsException, EntityCategoryNotFoundException, EntityNotFoundException, EntityServiceException,
            InvalidCRLGenerationInfoException, InvalidEntityException, InvalidEntityAttributeException, InvalidEntityCategoryException,
            InvalidProfileException, InvalidSubjectAltNameExtension, InvalidSubjectException, MissingMandatoryFieldException,
            ProfileNotFoundException, UnsupportedCRLVersionException {
        mockEntityManagementService.updateEntities(ents);
    }

    @Override
    public void deleteEntities(final Entities ents) throws EntityInUseException, EntityNotFoundException, EntityServiceException,
            InvalidEntityAttributeException, InvalidEntityAttributeException {
        mockEntityManagementService.deleteEntities(ents);
    }

    @Override
    public <T extends AbstractEntity> T updateEntity(final T t) throws AlgorithmNotFoundException, CRLExtensionException, CRLGenerationException,
            EntityAlreadyExistsException, EntityCategoryNotFoundException, EntityNotFoundException, EntityServiceException,
            InvalidCRLGenerationInfoException, InvalidEntityException, InvalidEntityAttributeException, InvalidEntityCategoryException,
            InvalidProfileException, InvalidSubjectAltNameExtension, InvalidSubjectException, MissingMandatoryFieldException,
            ProfileNotFoundException, UnsupportedCRLVersionException {
        return mockEntityManagementService.updateEntity(t);
    }

    @Override
    public <T extends AbstractEntity> T getEntity(final T t)
            throws EntityNotFoundException, EntityServiceException, InvalidEntityException, InvalidEntityAttributeException {
        return mockEntityManagementService.getEntity(t);
    }

    @Override
    public <T extends AbstractEntity> void deleteEntity(final T t) throws EntityAlreadyDeletedException, EntityInUseException,
            EntityNotFoundException, EntityServiceException, InvalidEntityException, InvalidEntityAttributeException {
        mockEntityManagementService.deleteEntity(t);
    }

    //    @Override
    //    public Entities getEntities(EntityType et)
    //            throws InternalServiceException {
    //        return mockEntityManagementService.getEntities(et);
    //    }
    @Override
    public List<? extends AbstractEntity> getEntitiesBySubject(final Subject sbjct, final EntityType et)
            throws EntityServiceException, InvalidSubjectException {
        return mockEntityManagementService.getEntitiesBySubject(sbjct, et);
    }

    @Override
    public boolean isEntityNameAvailable(final String string, final EntityType et) throws EntityServiceException, InvalidEntityException {
        return mockEntityManagementService.isEntityNameAvailable(string, et);
    }

    @Override
    public EnrollmentInfo getEnrollmentInfo(final EnrollmentType et, final Entity entity)
            throws EntityNotFoundException, EntityServiceException, EnrollmentURLNotFoundException, InvalidEntityException,
            InvalidEntityAttributeException, OTPExpiredException, TrustDistributionPointURLNotFoundException {
        return mockEntityManagementService.getEnrollmentInfo(et, entity);
    }

    @Override
    public boolean isOTPValid(final String string, final String string1) throws EntityNotFoundException, EntityServiceException, OTPExpiredException {
        return mockEntityManagementService.isOTPValid(string, string1);
    }

    @Override
    public void updateOTP(final String string, final String string1, final int i)
            throws EntityNotFoundException, EntityServiceException, InvalidOTPCountException, InvalidOTPException {
        mockEntityManagementService.updateOTP(string, string1, i);
    }

    @Override
    public String getOTP(final String string) throws EntityNotFoundException, EntityServiceException, OTPExpiredException, OTPNotSetException {
        return mockEntityManagementService.getOTP(string);
    }

    //	/* (non-Javadoc)
    //	 * @see com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService#getCAHierarchies()
    //	 */
    //	@Override
    //	public List<TreeNode<CAEntity>> getCAHierarchies()
    //			throws EntityNotFoundException, EntityServiceException {
    //		// TODO Auto-generated method stub
    //		return null;
    //	}
    //
    //	/* (non-Javadoc)
    //	 * @see com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService#getCAHierarchyByName(java.lang.String)
    //	 */
    //	@Override
    //	public TreeNode<CAEntity> getCAHierarchyByName(String arg0)
    //			throws EntityNotFoundException, EntityServiceException {
    //		// TODO Auto-generated method stub
    //		return null;
    //	}

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService#getEntities(com.ericsson.oss.itpf.security.pki.manager
     * .model.EntityType[])
     */
    @Override
    public Entities getEntities(final EntityType... arg0) throws EntityServiceException, InvalidEntityException, InvalidEntityAttributeException {
        return mockEntityManagementService.getEntities(arg0);
    }

    @Override
    public List<Entity> getEntitiesByCategory(final EntityCategory ec) throws EntityCategoryNotFoundException, EntityServiceException,
            InvalidEntityException, InvalidEntityAttributeException, InvalidEntityCategoryException {
        return mockEntityManagementService.getEntitiesByCategory(ec);
    }

    @Override
    public <T extends AbstractEntity> TDPSUrlInfo getTrustDistributionPointUrls(final T caEntity, final String issuerName, final CertificateStatus cs)
            throws EntityNotFoundException, EntityServiceException, TrustDistributionPointURLNotDefinedException,
            TrustDistributionPointURLNotFoundException {
        return mockEntityManagementService.getTrustDistributionPointUrls(caEntity, issuerName, cs);
    }

    @Override
    public <T extends AbstractEntity> String getTrustDistributionPointUrl(final T t, final String n, final CertificateStatus s)
            throws EntityNotFoundException, EntityServiceException, TrustDistributionPointURLNotDefinedException,
            TrustDistributionPointURLNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getEntityNameByIssuerNameAndSerialNumber(final String string, final String string1)
            throws CANotFoundException, SerialNumberNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getEntityNameListByIssuerName(final String string) throws CANotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getEntityNameListByTrustProfileName(final String string) throws ProfileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Entity> getEntityListByIssuerName(final String string) throws CANotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService#createEntityAndGetEnrollmentInfo(com.ericsson.oss.itpf
     * .security.pki.manager.model.entities.Entity, com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentType)
     */
    @Override
    public EntityEnrollmentInfo createEntityAndGetEnrollmentInfo(final Entity entity, final EnrollmentType enrollmentType)
            throws AlgorithmNotFoundException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException,
            EntityCategoryNotFoundException, EntityNotFoundException, EntityServiceException, InvalidCRLGenerationInfoException,
            InvalidEntityException, InvalidEntityAttributeException, InvalidEntityCategoryException, InvalidProfileException,
            InvalidSubjectAltNameExtension, InvalidSubjectException, MissingMandatoryFieldException, ProfileNotFoundException,
            UnsupportedCRLVersionException {
        // TODO Auto-generated method stub
        return mockEntityManagementService.createEntityAndGetEnrollmentInfo(entity, enrollmentType);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService#getCAHierarchies()
     */
    @Override
    public List<TreeNode<CAEntity>> getCAHierarchies() throws EntityNotFoundException, EntityServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService#getCAHierarchyByName(java.lang.String)
     */
    @Override
    public TreeNode<CAEntity> getCAHierarchyByName(final String arg0) throws EntityNotFoundException, EntityServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService#getTrustDistributionPointUrls(com.ericsson.oss.itpf.
     * security.pki.manager.model.entities.AbstractEntity)
     */
    //    @Override
    //    public <T extends AbstractEntity> List<TrustedEntityInfo> getTrustDistributionPointUrls(
    //            T arg0) throws EntityNotFoundException, EntityServiceException,
    //            TrustDistributionPointURLNotDefinedException,
    //            TrustDistributionPointURLNotFoundException {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.
     * EntityManagementService#getEntitiesByCategoryWithInvalidCertificate(java. util.Date, int,
     * com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory[])
     */
    @Override
    public List<Entity> getEntitiesWithInvalidCertificate(final Date arg0, final int arg1, final EntityCategory... arg2)
            throws EntityCategoryNotFoundException, EntityServiceException, MissingMandatoryFieldException {
        return mockEntityManagementCustomService.getEntitiesWithInvalidCertificate(arg0, arg1, arg2);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService#updateEntityAndGetEnrollmentInfo(com.ericsson.oss.itpf
     * .security.pki.manager.model.entities.Entity, com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentType)
     */
    @Override
    public EntityEnrollmentInfo updateEntityAndGetEnrollmentInfo(final Entity entity, final EnrollmentType enrollmentType)
            throws AlgorithmNotFoundException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException,
            EntityCategoryNotFoundException, EntityNotFoundException, EntityServiceException, InvalidCRLGenerationInfoException,
            InvalidEntityException, InvalidEntityAttributeException, InvalidEntityCategoryException, InvalidProfileException,
            InvalidSubjectAltNameExtension, InvalidSubjectException, MissingMandatoryFieldException, ProfileNotFoundException,
            UnsupportedCRLVersionException {
        // TODO Auto-generated method stub
        return mockEntityManagementService.updateEntityAndGetEnrollmentInfo(entity, enrollmentType);
    }

    @Override
    public List<TrustedEntityInfo> getTrustedEntitiesInfo(final EntityType et)
            throws CertificateNotFoundException, EntityNotFoundException, EntityServiceException, TrustDistributionPointURLNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TrustedEntityInfo> getTrustedEntitiesInfo(final EntityType et, final String entityName)
            throws CertificateNotFoundException, EntityNotFoundException, EntityServiceException, TrustDistributionPointURLNotFoundException {
        return mockEntityManagementService.getTrustedEntitiesInfo(et, entityName);
    }

    @Override
    public List<TrustedEntityInfo> getTrustedEntitiesInfo(final EntityType et, final CertificateStatus cs)
            throws CertificateNotFoundException, EntityNotFoundException, EntityServiceException, TrustDistributionPointURLNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<List<TrustedEntityInfo>> getTrustedEntitiesInfoChain(final EntityType entityType, final String entityName,
                                                                     final CertificateStatus... certificateStatus)
            throws CertificateNotFoundException, EntityNotFoundException, EntityServiceException, TrustDistributionPointURLNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long updateEntityStatusByCategoryAndStatus(EntityStatus entityStatus, EntityStatus entityStatus1, EntityCategory entityCategory, String s) throws EntityServiceException {
        return 0;
    }


    @Override
    public EntityEnrollmentInfo createEntityAndGetEnrollmentInfo_v1(final Entity entity, final EnrollmentType enrollmentType) throws AlgorithmNotFoundException,
            CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityCategoryNotFoundException, EntityNotFoundException,
            EntityServiceException, InvalidCRLGenerationInfoException, InvalidEntityException, InvalidEntityAttributeException,
            InvalidEntityCategoryException, InvalidProfileException, InvalidSubjectAltNameExtension, InvalidSubjectException,
            MissingMandatoryFieldException, OTPException, ProfileNotFoundException, UnsupportedCRLVersionException {
        return mockEntityManagementService.createEntityAndGetEnrollmentInfo_v1(entity, enrollmentType);
    }

  
    @Override
    public <T extends AbstractEntity> T createEntity_v1(final T entity)
            throws AlgorithmNotFoundException, CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityServiceException,
            EntityCategoryNotFoundException, InvalidCRLGenerationInfoException, InvalidEntityException, InvalidEntityAttributeException,
            InvalidEntityCategoryException, InvalidProfileException, InvalidSubjectAltNameExtension, InvalidSubjectException,
            MissingMandatoryFieldException, OTPException, ProfileNotFoundException, UnsupportedCRLVersionException {
       return mockEntityManagementService.createEntity_v1(entity);
    }

    @Override
    public EntityEnrollmentInfo updateEntityAndGetEnrollmentInfo_v1(final Entity entity, final EnrollmentType enrollmentType) throws AlgorithmNotFoundException,
            CRLExtensionException, CRLGenerationException, EntityAlreadyExistsException, EntityCategoryNotFoundException, EntityNotFoundException,
            EntityServiceException, InvalidCRLGenerationInfoException, InvalidEntityException, InvalidEntityAttributeException,
            InvalidEntityCategoryException, InvalidProfileException, InvalidSubjectAltNameExtension, InvalidSubjectException,
            MissingMandatoryFieldException, OTPException, ProfileNotFoundException, UnsupportedCRLVersionException {
    	return mockEntityManagementService.updateEntityAndGetEnrollmentInfo_v1(entity, enrollmentType);
    }

  
    @Override
    public <T extends AbstractEntity> T updateEntity_v1(final T entity) throws AlgorithmNotFoundException, CRLExtensionException,
            CRLGenerationException, EntityAlreadyExistsException, EntityCategoryNotFoundException, EntityNotFoundException, EntityServiceException,
            InvalidCRLGenerationInfoException, InvalidEntityException, InvalidEntityAttributeException, InvalidEntityCategoryException,
            InvalidProfileException, InvalidSubjectAltNameExtension, InvalidSubjectException, MissingMandatoryFieldException, OTPException,
            ProfileNotFoundException, UnsupportedCRLVersionException {
        return mockEntityManagementService.updateEntity_v1(entity);
    }

   
    @Override
    public void updateOTP(final String entityName, final String otp, final int otpCount, final int otpValidityPeriod)
            throws EntityNotFoundException, EntityServiceException, InvalidOTPCountException, InvalidOTPException, InvalidOtpValidityPeriodException {
    	mockEntityManagementService.updateOTP(entityName, otp, otpCount, otpValidityPeriod);
    }

    @Override
    public List<Entity> getEntitiesSummaryByCategory(EntityCategory ec) throws EntityCategoryNotFoundException, EntityServiceException, InvalidEntityException, InvalidEntityAttributeException, InvalidEntityCategoryException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends AbstractEntity> T getEntityForImport(T entity) throws EntityNotFoundException, EntityServiceException, InvalidEntityException, InvalidEntityAttributeException {
        return mockEntityManagementService.getEntityForImport(entity);
    }

    @Override
    public Entities getEntitiesForImport(EntityType... entityType) throws EntityServiceException, InvalidEntityException, InvalidEntityAttributeException {
        return mockEntityManagementService.getEntitiesForImport(entityType);
    }

    @Override
    public List<Entity> getEntitiesByCategoryv1(final EntityCategory ec) throws EntityCategoryNotFoundException, EntityServiceException,
            InvalidEntityException, InvalidEntityAttributeException, InvalidEntityCategoryException {

        return mockEntityManagementService.getEntitiesByCategoryv1(ec);
    }

}
