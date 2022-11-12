function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

function sendMsgApi(data) {
    return $axios({
        timeout: 6*60*1000, //响应时间
        'url':'/user/sendMsg',
        'method':'post',
        data
    })
}

  