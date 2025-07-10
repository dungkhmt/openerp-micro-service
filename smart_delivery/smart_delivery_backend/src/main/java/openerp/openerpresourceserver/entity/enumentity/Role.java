package openerp.openerpresourceserver.entity.enumentity;

public enum Role {
        SHIPPER, COLLECTOR, DRIVER, ADMIN, HUB_STAFF, HUB_MANAGER, CUSTOMER;
        public String toKeycloakRole() {
                return this.name();
        }
}
