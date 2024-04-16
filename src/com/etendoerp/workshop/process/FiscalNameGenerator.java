package com.etendoerp.workshop.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import org.openbravo.model.common.businesspartner.Category;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.service.db.DalBaseProcess;
import java.util.List;


public class FiscalNameGenerator extends DalBaseProcess {

    private static final Logger log = LogManager.getLogger();

    @Override
    protected void doExecute(ProcessBundle bundle) throws Exception {
        try {
            final OBCriteria<Organization> organizationOBCriteria = OBDal.getInstance().createCriteria(Organization.class)
                    .add(Restrictions.ilike(Organization.PROPERTY_SEARCHKEY, "F&B España, S.A"));
            Organization organization = (Organization) organizationOBCriteria.setMaxResults(1).uniqueResult();
            String organizationId = organization.getId();

            final OBCriteria<Category> categoryOBCriteria = OBDal.getInstance().createCriteria(Category.class)
                    .add(Restrictions.ilike(Category.PROPERTY_SEARCHKEY, "Employee"));
            Category category = (Category) categoryOBCriteria.setMaxResults(1).uniqueResult();
            String categoryId = category.getId();

            final OBCriteria<BusinessPartner> businessPartnerOBCriteria = OBDal.getInstance().createCriteria(BusinessPartner.class)
                    .add(Restrictions.eq(BusinessPartner.PROPERTY_ORGANIZATION + ".id", organizationId))
                    .add(Restrictions.eq(BusinessPartner.PROPERTY_BUSINESSPARTNERCATEGORY + ".id", categoryId));
            List<BusinessPartner> businessPartnerList = businessPartnerOBCriteria.list();

            for(BusinessPartner businessPartner : businessPartnerList){
                businessPartner.setName2(businessPartner.getSearchKey() + " " +  businessPartner.getName());
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
