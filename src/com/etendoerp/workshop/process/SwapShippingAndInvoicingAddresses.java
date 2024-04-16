package com.etendoerp.workshop.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.openbravo.client.application.process.BaseProcessActionHandler;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import org.openbravo.model.common.businesspartner.Location;

import java.util.List;
import java.util.Map;

public class SwapShippingAndInvoicingAddresses extends BaseProcessActionHandler {

    private static final Logger log = LogManager.getLogger();

    @Override
    protected JSONObject doExecute(Map<String, Object> parameters, String content) {
        JSONObject jsonRequest = null;
        OBContext.setAdminMode(true);
        try {
            jsonRequest = new JSONObject(content);
            String strBpartnerId = jsonRequest.getString("C_BPartner_ID");
            BusinessPartner businessPartner = (BusinessPartner) OBDal.getInstance().get(BusinessPartner.class, strBpartnerId);
            String description = businessPartner.getDescription();
            if (!description.contains("changed addresses"))
                businessPartner.setDescription(description + " changed addresses");
            List<Location> locations = OBDal.getInstance().createCriteria(Location.class)
                    .add(Restrictions.eq(Location.PROPERTY_BUSINESSPARTNER + ".id", strBpartnerId))
                    .list();
            for(Location local : locations){
                boolean isShipToAddress = local.isShipToAddress();
                local.setShipToAddress(!isShipToAddress);
                local.setInvoiceToAddress(isShipToAddress);
            }
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
        return jsonRequest;
    }
}
