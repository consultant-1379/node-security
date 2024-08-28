/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.test.pki;

import java.util.List;

import javax.ejb.Stateless;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.security.pki.common.model.Algorithm;
import com.ericsson.oss.itpf.security.pki.common.model.AlgorithmType;
import com.ericsson.oss.itpf.security.pki.manager.configurationmanagement.api.PKIConfigurationManagementService;
import com.ericsson.oss.itpf.security.pki.manager.exception.configuration.InvalidConfigurationException;
import com.ericsson.oss.itpf.security.pki.manager.exception.configuration.PKIConfigurationServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.configuration.algorithm.AlgorithmNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.category.EntityCategoryAlreadyExistsException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.category.EntityCategoryInUseException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.category.EntityCategoryNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.category.InvalidEntityCategoryException;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.services.security.pkimock.api.MockConfigurationManagementService;

/**
 *
 * @author enmadmin
 */
@Stateless
public class TestPkiConfigurationManagementService implements PKIConfigurationManagementService {

    @EServiceRef
    MockConfigurationManagementService mockConfigService;

    @Override
    public List<Algorithm> getAlgorithmsByType(final AlgorithmType... ats) throws AlgorithmNotFoundException, PKIConfigurationServiceException {
        return mockConfigService.getAlgorithmsByType(ats);
    }

    @Override
    public List<Algorithm> getSupportedAlgorithmsByType(final AlgorithmType... ats)
            throws AlgorithmNotFoundException, PKIConfigurationServiceException {
        return mockConfigService.getSupportedAlgorithmsByType(ats);
    }

    @Override
    public void updateAlgorithms(final List<Algorithm> list)
            throws AlgorithmNotFoundException, PKIConfigurationServiceException, InvalidConfigurationException {
        mockConfigService.updateAlgorithms(list);
    }

    @Override
    public Algorithm getAlgorithmByNameAndKeySize(final String string, final Integer intgr)
            throws AlgorithmNotFoundException, PKIConfigurationServiceException {
        return mockConfigService.getAlgorithmByNameAndKeySize(string, intgr);
    }

    @Override
    public List<Algorithm> getAlgorithmsByName(final String string) throws AlgorithmNotFoundException, PKIConfigurationServiceException {
        return mockConfigService.getAlgorithmsByName(string);
    }

    @Override
    public EntityCategory createCategory(final EntityCategory ec)
            throws EntityCategoryAlreadyExistsException, InvalidEntityCategoryException, PKIConfigurationServiceException {
        return mockConfigService.createCategory(ec);
    }

    @Override
    public EntityCategory updateCategory(final EntityCategory ec)
            throws EntityCategoryAlreadyExistsException, InvalidEntityCategoryException, PKIConfigurationServiceException {
        return mockConfigService.updateCategory(ec);
    }

    @Override
    public EntityCategory getCategory(final EntityCategory ec) throws EntityCategoryNotFoundException, PKIConfigurationServiceException {
        return mockConfigService.getCategory(ec);
    }

    @Override
    public void deleteCategory(final EntityCategory ec)
            throws EntityCategoryNotFoundException, EntityCategoryInUseException, PKIConfigurationServiceException {
        mockConfigService.deleteCategory(ec);
    }

    @Override
    public boolean isCategoryNameAvailable(final String string) throws PKIConfigurationServiceException {
        return mockConfigService.isCategoryNameAvailable(string);
    }

    @Override
    public List<EntityCategory> listAllEntityCategories() throws PKIConfigurationServiceException {
        return mockConfigService.listAllEntityCategories();
    }

}
