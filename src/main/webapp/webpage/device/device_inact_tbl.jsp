<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<style type="text/css">
    .device_inact_tbl_box {
        padding: 30px;
        text-align: center;
    }

    .device_inact_tbl_box .el-table {
        margin: 0 auto;
    }
</style>
<div class="device_inact_tbl_box">
    <div id="device_inact_tbl">
        <template>
            <el-table :data="tableData" style="width: 100%" stripe>
                <el-table-column prop="id" label="ID" width="150">
                </el-table-column>
                <el-table-column prop="device_sn" label="设备序列号" width="200">
                </el-table-column>
                <el-table-column prop="status" label="状态" width="150">
                </el-table-column>
                <el-table-column prop="online" label="在线" width="150">
                </el-table-column>
                <el-table-column label="注册时间">
                    <template slot-scope="scope">
                        <i class="el-icon-time"></i>
                        <span style="margin-left: 10px">{{ scope.row.register_time|formatDate }}</span>
                    </template>
                </el-table-column>
            </el-table>
        </template>

        <template>
            <div class="block">
                <el-pagination ref="pagination"
                               @size-change="handleChange"
                               @current-change="handleChange"
                               :current-page.sync="currentPage"
                               :page-size.sync="pageSize"
                               :page-sizes="pageSizes"
                               prev-text="上一页"
                               next-text="下一页"
                               layout="total, sizes, prev, pager, next, jumper"
                               :total="total">
                </el-pagination>
            </div>
        </template>
    </div>
</div>

<script type="text/javascript">
    var vm = new Vue({
        el: "#device_inact_tbl",
        data: {
            tableData: [],
            searching: true,
            currentPage: 1,
            pageSizes: [5, 10, 20],
            pageSize: 10,
            total:''
        },
        methods: {
            handleChange(val) {
                ajaxInActDeviceList(this.currentPage, this.pageSize);
            }
        },
        filters: {
            formatDate: function (time) {
                var data = new Date(time);
                return formatDate(data, 'yyyy-MM-dd hh:mm:ss');
            }
        }
    });

    ajaxInActDeviceList(vm.currentPage, vm.pageSize);

    function ajaxInActDeviceList(pageNum, pageSize) {
        ajaxGet({
            url: "${pageContext.request.contextPath}/device/inact_list",
            data: {
                pageNum: pageNum,
                pageSize: pageSize
            },
            success: function (result) {
                vm.tableData = result.data.list;
                vm.total = result.data.total;
            }
        });
    }

</script>
