/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.test.pki;

import java.util.List;

import javax.ejb.Stateless;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.pki.manager.exception.configuration.algorithm.AlgorithmNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.caentity.CANotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.caentity.InvalidCAException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.category.EntityCategoryNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.category.InvalidEntityCategoryException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.InvalidProfileAttributeException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.InvalidProfileException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.ProfileAlreadyExistsException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.ProfileInUseException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.ProfileNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.profile.ProfileServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.CertificateExtensionException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.InvalidSubjectException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.MissingMandatoryFieldException;
import com.ericsson.oss.itpf.security.pki.manager.exception.security.certificate.certificatefield.UnSupportedCertificateVersion;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.ProfileType;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.AbstractProfile;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.Profiles;
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.ProfileManagementService;
import com.ericsson.oss.services.security.pkimock.api.MockProfileManagementService;

/**
 *
 * @author enmadmin
 */
@Stateless
public class TestPkiProfileManagementServiceImpl implements ProfileManagementService {

    @EServiceRef
    MockProfileManagementService mockProfileManagementService;

    @Override
    public void importProfiles(final Profiles prfls) throws AlgorithmNotFoundException, CANotFoundException, CertificateExtensionException,
            EntityCategoryNotFoundException, InvalidCAException, InvalidEntityCategoryException, InvalidProfileException,
            InvalidProfileAttributeException, InvalidSubjectException, MissingMandatoryFieldException, ProfileAlreadyExistsException,
            ProfileNotFoundException, ProfileServiceException, UnSupportedCertificateVersion {
        mockProfileManagementService.importProfiles(prfls);
    }

    //    @Override
    //    public Profiles exportProfiles(ProfileType pt)
    //            throws InternalServiceException {
    //        return mockProfileManagementService.exportProfiles(pt);
    //    }

    @Override
    public void updateProfiles(final Profiles prfls) throws AlgorithmNotFoundException, CANotFoundException, CertificateExtensionException,
            EntityCategoryNotFoundException, InvalidCAException, InvalidEntityCategoryException, InvalidProfileException,
            InvalidProfileAttributeException, InvalidSubjectException, MissingMandatoryFieldException, ProfileAlreadyExistsException,
            ProfileNotFoundException, ProfileServiceException, UnSupportedCertificateVersion {
        mockProfileManagementService.updateProfiles(prfls);
    }

    @Override
    public void deleteProfiles(final Profiles prfls)
            throws InvalidProfileException, ProfileInUseException, ProfileNotFoundException, ProfileServiceException {
        mockProfileManagementService.deleteProfiles(prfls);
    }

    @Override
    public <T extends AbstractProfile> T createProfile(final T t) throws AlgorithmNotFoundException, CANotFoundException,
            CertificateExtensionException, EntityCategoryNotFoundException, InvalidCAException, InvalidEntityCategoryException,
            InvalidProfileException, InvalidProfileAttributeException, InvalidSubjectException, MissingMandatoryFieldException,
            ProfileAlreadyExistsException, ProfileNotFoundException, ProfileServiceException, UnSupportedCertificateVersion {
        return mockProfileManagementService.createProfile(t);
    }

    @Override
    public <T extends AbstractProfile> T updateProfile(final T t) throws AlgorithmNotFoundException, CANotFoundException,
            CertificateExtensionException, EntityCategoryNotFoundException, InvalidCAException, InvalidEntityCategoryException,
            InvalidProfileException, InvalidProfileAttributeException, InvalidSubjectException, MissingMandatoryFieldException,
            ProfileAlreadyExistsException, ProfileNotFoundException, ProfileServiceException, UnSupportedCertificateVersion {
        return mockProfileManagementService.updateProfile(t);
    }

    @Override
    public <T extends AbstractProfile> T getProfile(final T t) throws InvalidProfileException, InvalidProfileAttributeException,
            ProfileNotFoundException, ProfileServiceException, MissingMandatoryFieldException {
        return mockProfileManagementService.getProfile(t);
    }

    @Override
    public <T extends AbstractProfile> void deleteProfile(final T t)
            throws InvalidProfileException, ProfileInUseException, ProfileNotFoundException, ProfileServiceException {
        mockProfileManagementService.deleteProfile(t);
    }

    @Override
    public <T extends AbstractProfile> boolean isProfileNameAvailable(final String string, final ProfileType pt)
            throws InvalidProfileException, ProfileServiceException {
        return mockProfileManagementService.isProfileNameAvailable(string, pt);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.ProfileManagementService#exportProfiles(com.ericsson.oss.itpf.security.pki.
     * manager.model.ProfileType[])
     */
    @Override
    public Profiles exportProfiles(final ProfileType... profileTypes)
            throws InvalidProfileException, InvalidProfileAttributeException, ProfileServiceException {
        return mockProfileManagementService.exportProfiles(profileTypes);
    }

    @Override
    public List<EntityProfile> getProfilesByCategory(final EntityCategory ec)
            throws EntityCategoryNotFoundException, ProfileNotFoundException, ProfileServiceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Profiles getActiveProfiles(final ProfileType... pts) throws ProfileServiceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Profiles exportProfilesForImport(ProfileType... profileType) throws InvalidProfileException, InvalidProfileAttributeException, ProfileServiceException {
        return mockProfileManagementService.exportProfilesForImport(profileType);
    }

    @Override
    public <T extends AbstractProfile> T getProfileForImport(T profile)
            throws InvalidProfileException, InvalidProfileAttributeException, ProfileNotFoundException, ProfileServiceException, MissingMandatoryFieldException {
        return mockProfileManagementService.getProfileForImport(profile);
    }
}
