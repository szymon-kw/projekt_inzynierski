package pl.projekt_inzynierski.Dto;

import org.springframework.data.domain.Page;

public class FinalListViewDto {

    private Page<ListViewDto> element;
    private long totalPending;
    private long totalUnderReview;
    private long totalCompleted;
    private long totalUnAssigned;
    private boolean showTimetoClose;

    public Page<ListViewDto> getElement() {
        return element;
    }

    public void setElement(Page<ListViewDto> element) {
        this.element = element;
    }

    public long getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(long totalPending) {
        this.totalPending = totalPending;
    }

    public long getTotalUnderReview() {
        return totalUnderReview;
    }

    public void setTotalUnderReview(long totalUnderReview) {
        this.totalUnderReview = totalUnderReview;
    }

    public long getTotalCompleted() {
        return totalCompleted;
    }

    public void setTotalCompleted(long totalCompleted) {
        this.totalCompleted = totalCompleted;
    }

    public long getTotalUnAssigned() {
        return totalUnAssigned;
    }

    public void setTotalUnAssigned(long totalUnAssigned) {
        this.totalUnAssigned = totalUnAssigned;
    }

    public boolean isShowTimetoClose() {
        return showTimetoClose;
    }

    public void setShowTimetoClose(boolean showTimetoClose) {
        this.showTimetoClose = showTimetoClose;
    }
}
