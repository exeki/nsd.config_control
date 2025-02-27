def logs = utils.find('adminLogRecord', ['category' : 'importMetainfoStart', "actionDate" : op.gt(new Date().parse('dd.MM.yyyy HH:mm:ss.SSS', '%date'))])
if(logs.size() == 1) {
    def log = logs.last().subMap(['actionType', 'authorIP', 'description', 'authorLogin', 'UUID', 'category', 'actionDate', 'categoryName'])
    return [ 'log' : log, 'result' : 1]
} else if(logs.size() == 0)  return ['result' : 0]
  else return ['result' : 2]