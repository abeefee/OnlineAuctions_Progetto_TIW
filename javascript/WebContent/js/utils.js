/**
 * AJAX call management
 */

function makeCall(method, url, form, callback, reset = true){
	var req = new XMLHttpRequest();
		
	req.onreadystatechange = function(){
		switch(req.readyState){
			case XMLHttpRequest.DONE:
				callback(req);	
		}
	}; //closure
	
	req.open(method, url);
	
	if(form == null){
		req.send();
	} else{
		req.send(new FormData(form));
	}
	
	if(form !== null && reset === true){
		form.reset();
	}
}

function makeCallString(method, url, string, callback){
	var req = new XMLHttpRequest();
	
	req.onreadystatechange = function(){
		switch(req.readyState){
			case XMLHttpRequest.DONE:
				callback(req);	
		}
	}; //closure
	
	req.open(method, url);
	
	req.setRequestHeader('Content-Type', 'text/plain');
	req.send(string);
}