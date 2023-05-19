package openerp.containertransport.service;

import openerp.containertransport.dto.TrailerFilterRequestDTO;
import openerp.containertransport.dto.TrailerModel;

import java.util.List;

public interface TrailerService {
    TrailerModel createTrailer(TrailerModel trailerModel);
    TrailerModel getTrailerById(long id);
    TrailerModel updateTrailer(TrailerModel trailerModel);
    List<TrailerModel> filterTrailer(TrailerFilterRequestDTO trailerFilterRequestDTO);
}
