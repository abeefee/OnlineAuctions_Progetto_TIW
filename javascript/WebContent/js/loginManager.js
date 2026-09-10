/**
 * Login manager
 */

(function() {
	const login_button = document.getElementById("login-btn");
	const login_warning_div = document.getElementById("login_warning_div");
	
	const open_register_button = document.getElementById("open_register_button");
	const register_div = document.getElementById("register-div");
	
	const register_button = document.getElementById("register-btn");
	const register_warning_div = document.getElementById("register_warning_div");
		
	login_button.addEventListener('click', (e) => {
		console.log("Click login");
		var form = e.target.closest("form");
		if(form.checkValidity()){
			console.log("Form login valido");
			sendToServer(form, login_warning_div, 'Login');
		} else{
			form.reportValidity();
		}
	});
	
	open_register_button.addEventListener('click', (e) => {
		if(e.target.textContent === "Register now!"){
            e.target.textContent = "Hide register form";
            register_div.style.display = 'block';
        }else{
            e.target.textContent = "Register now!";
            register_div.style.display = 'none';
        }
	});
	
	register_button.addEventListener('click', (e) => {
		console.log("Click reg");
		var form = e.target.closest("form");
		register_warning_div.style.display = 'none';
		if(form.checkValidity()){
			console.log("Form reg valido");
			sendToServer(form, register_warning_div, 'Register');
		} else{
			form.reportValidity();
		}
	});
	
	function sendToServer(form, error_div, request_url){
        makeCall("POST", request_url, form, function(req){
            switch(req.status){ //Get status code
                case 200: //Okay
                	console.log("200");
                    var userData = JSON.parse(req.responseText);
                    sessionStorage.setItem('id_user', userData.id_user);
                    sessionStorage.setItem('username', userData.username);
                    window.location.href = "home.html";
                    break;
                case 400: // bad request
                case 401: // unauthorized
                case 500: // server error
                    error_div.textContent = req.responseText;
                    error_div.style.display = 'block';
                    break;
                default: //Error
                	console.log("default");
                    error_div.textContent = "Request reported status " + req.status;
                    error_div.style.display = 'block';
            }
        });
    }
})();