		/**
        * 获取列表
        * @param map
        * @return
        */
       public JSONObject getGridReturn(Map<String,String> map){
             JSONObject gridReturn = new JSONObject();
             String sql = TagUtil.getGridSql(map,tableName);
             List<${entityName}> results = this.find(sql);
             List<Object> rows = TagUtil.getRtList(results);
             String countSql = TagUtil.getGridCountSql(map,tableName);
             ${entityName} ${entityLowerName} = this.findFirst(countSql);
             Long total = ${entityLowerName}.getLong("total");
             gridReturn.put("rows", rows);
             gridReturn.put("total", total);
             return gridReturn;
       }

