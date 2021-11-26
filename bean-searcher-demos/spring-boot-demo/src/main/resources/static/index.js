Array.prototype.removeAll = function () {
    this.splice(0, this.length);
};

Array.prototype.pushAll = function (arr) {
    var self = this;
    arr.forEach(function (item) {
        self.push(item);
    });
};

new Vue({
    el: '#app',
    data: {
        list: [],
        total: 0,
        sumAge: 0,
        loading: false,
        params: {
            'name': null,
            'name-op': 'in',
            'name-ic': false,
            'age-0': null,
            'age-1': null,
            'age-op': 'eq',
            'department': null,
            'department-op': 'in',
            'department-ic': false,
            'entryDate-0': null,
            'entryDate-1': null,
            'entryDate-op': 'bt',
            'sort': null,
            'order': null,
            'page': 0,
            'size': 5
        },
        strOps: [
            { key: 'eq', label: '等于'},
            { key: 'in', label: '在..列表中(多个值英文字符;分隔开)'},
            { key: 'like', label: '包含'},
            { key: 'sw', label: '以...开始'},
            { key: 'ew', label: '以...结束'}
        ],
        numOps: [
            { key: 'eq', label: '等于'},
            { key: 'gt', label: '大于'},
            { key: 'lt', label: '小于'},
            { key: 'ge', label: '大于等于'},
            { key: 'le', label: '小于等于'},
            { key: 'bt', label: '区间'}
        ],
        timeOps: [
            { key: 'bt', label: '区间'},
            { key: 'gt', label: '大于'},
            { key: 'lt', label: '小于'},
            { key: 'ge', label: '大于等于'},
            { key: 'le', label: '小于等于'}
        ]
    },
    mounted() {
        this.loadData();
    },
    methods: {
        loadData() {
            this.loading = true;
            var self = this;
            $.get('/employee/index', this.params, function (data) {
                self.list.removeAll();
                self.list.pushAll(data.dataList);
                self.total = data.totalCount;
                self.sumAge = data.summaries[0];
                self.loading = false;
            });
        },
        handleFilter() {
            this.params.page = 0;
            this.loadData();
        },
        sortChange(obj) {
            this.params.sort = obj.prop;
            if (obj.order === 'descending') {
                this.params.order = 'desc';
            } else if (obj.order === 'ascending') {
                this.params.order = 'asc';
            } else {
                this.params.order = null;
            }
            this.loadData();
        }
    }
});