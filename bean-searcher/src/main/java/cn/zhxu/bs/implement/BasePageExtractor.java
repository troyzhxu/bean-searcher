package cn.zhxu.bs.implement;

import cn.zhxu.bs.IllegalParamException;
import cn.zhxu.bs.PageExtractor;
import cn.zhxu.bs.param.Paging;
import cn.zhxu.bs.util.MapBuilder;
import cn.zhxu.bs.util.ObjectUtils;

import java.util.Map;

public abstract class BasePageExtractor implements PageExtractor {

    /**
     * 开始页，或开始条数
     * */
    private int start = 0;
    
    /**
     * 最大条数字段参数名
     */
    private String sizeName = "size";

    /**
     * 默认分页大小
     */
    private int defaultSize = 15;

    /**
     * 最大允许查询条数
     */
    private int maxAllowedSize = 100;

    /**
     * 分页保护：最大允许偏移量，如果是 page 分页，则最大允许页码是 maxAllowedOffset / size
     * @since v3.8.1
     */
    private long maxAllowedOffset = 20000;

    @Override
    public Paging extract(Map<String, Object> paraMap) throws IllegalParamException {
        Paging paging = doExtract(paraMap);
        int size = paging.getSize();
        if (size < 0 || size > maxAllowedSize) {
            throw new IllegalParamException("Invalid page size: " + size + ", it must between 0 and " + maxAllowedSize);
        }
        long offset = paging.getOffset();
        if (offset < 0 || offset > maxAllowedOffset) {
            throw new IllegalParamException("Invalid page offset: " + offset + ", it must between 0 and " + maxAllowedOffset);
        }
        return paging;
    }

    private Paging doExtract(Map<String, Object> paraMap) {
        Object value = paraMap.get(MapBuilder.PAGING);
        if (value instanceof MapBuilder.Page) {
            return toPaging((MapBuilder.Page) value);
        }
        if (value instanceof MapBuilder.Limit) {
            return toPaging((MapBuilder.Limit) value);
        }
        int size = toSize(paraMap.get(getSizeName()));
        return new Paging(size, toOffset(paraMap, size));
    }

    protected abstract long toOffset(Map<String, Object> paraMap, int size);

    protected int toSize(Object value) {
        Integer size = ObjectUtils.toInt(value);
        if (size == null) {
            return defaultSize;
        }
        return size;
    }

    protected Paging toPaging(MapBuilder.Page page) {
        int size = page.getSize();
        long pageNo = Math.max(page.getPage() - start, 0);
        return new Paging(size, size * pageNo);
    }

    protected Paging toPaging(MapBuilder.Limit limit) {
        int size = limit.getSize();
        long offset = Math.max(limit.getOffset() - start, 0);
        return new Paging(size, offset);
    }

    @Override
    public String getSizeName() {
        return sizeName;
    }
    
    public int getStart() {
        return start;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setMaxAllowedSize(int maxAllowedSize) {
        this.maxAllowedSize = maxAllowedSize;
    }

    public int getMaxAllowedSize() {
        return maxAllowedSize;
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
    }

    public long getMaxAllowedOffset() {
        return maxAllowedOffset;
    }

    public void setMaxAllowedOffset(long maxAllowedOffset) {
        this.maxAllowedOffset = maxAllowedOffset;
    }

}
