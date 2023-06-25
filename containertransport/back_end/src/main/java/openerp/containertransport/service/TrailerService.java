package openerp.containertransport.service;

import openerp.containertransport.dto.TrailerFilterRequestDTO;
import openerp.containertransport.dto.TrailerFilterRes;
import openerp.containertransport.dto.TrailerModel;

import java.util.List;

public interface TrailerService {
    TrailerModel createTrailer(TrailerModel trailerModel);
    TrailerModel getTrailerByUid(String uid);
    TrailerModel updateTrailer(TrailerModel trailerModel);
    TrailerFilterRes filterTrailer(TrailerFilterRequestDTO trailerFilterRequestDTO);
    TrailerModel deleteTrailer(String uid);
}
