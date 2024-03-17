package com.hust.openerp.taskmanagement.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public class PaginationDTO<T> {
  private int page;
  private int size;
  private long totalElements;
  private boolean last;
  private boolean first;
  private List<T> data;

  public PaginationDTO(int page, int size, long total, Iterable<T> data, boolean last, boolean first) {
    this.page = page;
    this.size = size;
    this.totalElements = total;
    this.data = (List<T>) data;
    this.last = last;
    this.first = first;
  }

  public PaginationDTO(Page<T> page) {
    this(page.getNumber(), page.getSize(), page.getTotalElements(), page.getContent(), page.isLast(), page.isFirst());
  }

  public int getPage() {
    return page;
  }

  public int getSize() {
    return size;
  }

  public long getTotalElements() {
    return totalElements;
  }

  public List<T> getData() {
    return data;
  }

  public boolean getLast() {
    return last;
  }

  public boolean getFirst() {
    return first;
  }
}
