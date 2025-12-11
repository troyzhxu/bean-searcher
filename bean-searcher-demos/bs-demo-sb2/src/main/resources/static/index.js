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
            'name-op': 'ct',
            'name-ic': false,
            'age-0': null,
            'age-1': null,
            'age-op': 'eq',
            'department': null,
            'department-op': 'ct',
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
            { key: 'ct', label: '包含'},
            { key: 'sw', label: '以...开始'},
            { key: 'ew', label: '以...结束'}
        ],
        nameOps: [
            { key: 'eq', label: '等于' , disabled: true},
            { key: 'ct', label: '包含'},
            { key: 'sw', label: '以...开始'},
            { key: 'ew', label: '以...结束', disabled: true}
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
        handleExport() {
            var queryString = this.objectToQueryString(this.params);
            location.href = '/employee/file.cvs?' + queryString;
        },
        objectToQueryString(params) {
          // 检查参数是否为有效对象
          if (!params || typeof params !== 'object' || Array.isArray(params)) {
            return '';
          }
          // 遍历对象的键值对，进行编码并拼接
          const queryParts = [];
          for (const key in params) {
            // 只处理对象自身的可枚举属性
            if (Object.prototype.hasOwnProperty.call(params, key)) {
              const value = params[key];
              // 跳过值为undefined的属性
              if (value === undefined || value == null) continue;
              // 对键和值进行URL编码
              const encodedKey = encodeURIComponent(key);
              const encodedValue = encodeURIComponent(value);
              queryParts.push(`${encodedKey}=${encodedValue}`);
            }
          }
          // 拼接所有部分，返回完整的queryString
          return queryParts.join('&');
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