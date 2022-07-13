package pub.zgq.community.entity;

/**
 * @Author 孑然
 *
 * 封装分页相关的信息
 */
public class Page {

    /**
     * 当前页码（前端传入）
     */
    private int current = 1;

    /**
     * 显示上限
     */
    private int limit = 10;

    /**
     * 数据总行数（后端查询，设置进来）
     */
    private int rows;

    /**
     * 查询路径（用于复用分页链接 --- 在controller中设置进来）
     */
    private String path;


    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     *
     * @return
     */
    public int getOffset() {
        // current  * limit - limit
        //注意数据库是从索引0开始的
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     *
     * @return
     */
    public int getTotal() {
        // row /limit [+1]
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }

    }

    /**
     * 获取显示的起始页码
     * 保证显示5个页码
     *
     * @return
     */
    public int getFrom() {
        if (getTotal() > 4) {
            if (current == getTotal() && current > 4) {
                return current - 4;
            } else if (current == getTotal() - 1) {
                return current - 3;
            }
        }
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取显示的结束页码
     * 保证显示5个页码
     *
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        if (current == 1) {
            to = current + 4;
            return to > total ? total : to;
        }
        if (current == 2) {
            to = current + 3;
            return to > total ? total : to;
        }
        return to > total ? total : to;
    }
}
