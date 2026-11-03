package com.smartfit.gymApp.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/** Una pagina acotada y su total, independiente de Swing y del acceso a datos. */
public record PageResult<T>(List<T> items, long total, int page, int pageSize) {
    public PageResult {
        items = List.copyOf(Objects.requireNonNull(items, "items"));
        if (total < 0 || page < 0 || pageSize < 1) throw new IllegalArgumentException("Paginacion invalida");
    }

    public int pageCount() {
        return total == 0 ? 1 : (int) Math.min(Integer.MAX_VALUE, 1 + (total - 1) / pageSize);
    }

    public boolean hasPrevious() { return page > 0; }
    public boolean hasNext() { return (long) page + 1 < pageCount(); }

    public <R> PageResult<R> map(Function<? super T, ? extends R> mapper) {
        List<R> mapped = new ArrayList<>(items.size());
        for (T item : items) mapped.add(mapper.apply(item));
        return new PageResult<>(mapped, total, page, pageSize);
    }
}
