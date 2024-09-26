package openerp.openerpresourceserver.service;

public interface RoleService {
    RoleService getByUserId(Integer id);
    RoleService save(RoleService roleService);
    void delete(Integer id);
}
