import com.fasterxml.jackson.databind.ObjectMapper

def logs = utils.find('adminLogRecord', ['category': 'importMetainfoDone', "actionDate": op.gt(new Date().parse('dd.MM.yyyy HH:mm:ss.SSS', '%date'))])
def result = ['log': null]
if (logs.size() == 1) {
    def log = logs.last().subMap(['actionType', 'authorIP', 'description', 'authorLogin', 'UUID', 'category', 'actionDate', 'categoryName'])
    result = ['log': log]
}
return new ObjectMapper().writeValueAsString(result)