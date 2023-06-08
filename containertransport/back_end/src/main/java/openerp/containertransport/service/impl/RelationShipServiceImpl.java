package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.entity.Relationship;
import openerp.containertransport.repo.RelationshipRepo;
import openerp.containertransport.service.RelationshipService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationShipServiceImpl implements RelationshipService {
    private final RelationshipRepo relationshipRepo;
    @Override
    public List<Relationship> getAllRelationShip() {
        List<Relationship> relationships = relationshipRepo.findAll();
        return relationships;
    }
}
