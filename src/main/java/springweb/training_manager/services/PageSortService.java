package springweb.training_manager.services;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class PageSortService {
    public static <PagedObj> int getPageNumber(Page<PagedObj> pages) {
        return Math.max(pages.getTotalPages() - 2, 0);
    }

    /**
     * This static method is responsible for checking if <code>Class< ClassType ></code>
     * contains fields sent as <code>Order</code> by checking if getters exists. If it
     * does not contain field, then the <code>Order</code> is ignored.
     *
     * @param entityClass class that is supposed to have fields like one in
     *                    <code>Order</code>
     * @param page        pageable object containing <code>Sort</code> with orders
     * @param logger      for displaying warning about not corresponding field to order
     * @param <ClassType> Class to check if contains fields
     *
     * @return new Pageable with validated order or <code>Pageable page</code> sent by
     * user
     */
    public static <ClassType> Pageable validateSort(
        Class<ClassType> entityClass,
        Pageable page,
        Logger logger
    ) {
        if (page.getSort()
            .isEmpty())
            return page;

        List<Sort.Order> orders = page.getSort()
            .get()
            .toList();
        List<Sort.Order> orderToSave = new ArrayList<>(orders.size());
        for (Sort.Order order : orders) {
            try {
                entityClass.getMethod("get" + capitalize(order.getProperty()));
                orderToSave.add(order);
            } catch (NoSuchMethodException e) {
                logger.warn(
                    "No such field '" + order.getProperty() +
                        "' in class '" + entityClass.getName() + "'"
                );
            }
        }

        return PageRequest.of(
            page.getPageNumber(),
            page.getPageSize(),
            Sort.by(orderToSave)
        );
    }

    public static <M, E> Page<M> getPageBy(
        Class<E> entityClass,
        Pageable page,
        Function<Pageable, Page<E>> find,
        Function<E, M> mapper,
        Logger logger
    ) {
        page = PageSortService.validateSort(entityClass, page, logger);

        Page<M> toReturn = find.apply(page)
            .map(mapper);
        if (toReturn.getContent()
            .isEmpty())
            toReturn = find.apply(
                    PageRequest.of(
                        PageSortService.getPageNumber(toReturn),
                        toReturn.getSize(),
                        page.getSort()
                    )
                )
                .map(mapper);
        return toReturn;
    }

    public static <M, E> Page<M> getPageBy(
        Class<E> entityClass,
        Pageable page,
        Function<Pageable, Page<M>> find,
        Logger logger
    ) {
        page = PageSortService.validateSort(entityClass, page, logger);

        Page<M> toReturn = find.apply(page);
        if (toReturn.getContent()
            .isEmpty())
            toReturn = find.apply(
                PageRequest.of(
                    PageSortService.getPageNumber(toReturn),
                    toReturn.getSize(),
                    page.getSort()
                )
            );
        return toReturn;
    }

    public static Sort.Order getSortModel(final Pageable page) {
        return getSortModel(page, "id");
    }

    public static Sort.Order getSortModel(
        final Pageable page,
        final String defaultSortField
    ) {
        var order = page.getSort()
            .get()
            .findFirst()
            .orElse(
                new Sort.Order(
                    Sort.Direction.ASC,
                    defaultSortField
                )
            );

        return order;
    }

    public static void setSortModels(
        final Pageable page,
        final Model model,
        final String defaultSortField
    ) {
        var order = getSortModel(page, defaultSortField);
        model.addAttribute("currOrder", order);
        model.addAttribute("newOrder", order.reverse());
    }

    private static String capitalize(String toCapitalize) {
        return toCapitalize.substring(0, 1)
            .toUpperCase() + toCapitalize.substring(1);
    }
}
