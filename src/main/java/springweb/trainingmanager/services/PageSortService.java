package springweb.trainingmanager.services;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class PageSortService {
    public static <PagedObj> int getPageNumber(Page<PagedObj> pages){
        return Math.max(pages.getTotalPages() - 2, 0);
    }
}
