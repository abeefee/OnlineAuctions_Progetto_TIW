/**
 * 
 */

(function(){
	var pageOrchestrator = new PageOrchestrator();
	const imgSrc = "/TIW_Project_Biffi_Firenze_2023_Pure_HTML/ImageFolder/";
	
	var cookieManager = new CookiesManager();
	 
	window.addEventListener("load", () => {
		pageOrchestrator.start(); //Initialize components
		pageOrchestrator.refresh(); //Display initial content 
	});
	 
	function PageOrchestrator(){
		
		var buy_button = document.getElementById("buy_button");
		var buy_div = document.getElementById("buy-div");
		
		var sell_button = document.getElementById("sell_button");
		var sell_div = document.getElementById("sell-div");
		
		this.start = function(){
			
			userInfo = new UserInfo(
				sessionStorage.getItem('username'),
				sessionStorage.getItem('id_user'),
				[document.getElementById("username"), document.getElementById("usernameHeader")],
				[document.getElementById("idUserHeader")],
				document.getElementById("logout-button")
			);
			 
			buyInfo = new BuyInfo(
				document.getElementById("buy-div"),
				document.getElementById("awardedAuctions-container"),
				document.getElementById("searchAuctions-button"),
				document.getElementById("searchAuctions-warning"),
				document.getElementById("searchedAuctions-container"),
				auctionBids = new AuctionBids(
					document.getElementById("auctionBids-container"),
					document.getElementById("auctionItems-container"),
					document.getElementById("currentBids-container"),
					document.getElementById("offerForm-container"),
					document.getElementById("minimumOffer"),
					document.getElementById("offerForm-id_auction"),
					document.getElementById("placeBid-button"),
					document.getElementById("placeBid-warning"),
					document.getElementById("auctionBids-warning")
				),
				visitedAuctionsInfo = new VisitedAuctionsInfo(
					document.getElementById("lastVisitedAuctionsId-container"),
					document.getElementById("lastVisitedWarning-container"),
					fastAuctionBids = new AuctionBids(
						document.getElementById("fastAuctionBids-container"),
						document.getElementById("fastAuctionItems-container"),
						document.getElementById("fastCurrentBids-container"),
						document.getElementById("fastOfferForm-container"),
						document.getElementById("fastMinimumOffer"),
						document.getElementById("fastOfferForm-id_auction"),
						document.getElementById("fastPlaceBid-button"),
						document.getElementById("fastPlaceBid-warning"),
						document.getElementById("fastAuctionBids-warning")
					)
				)
			);
						 
			sellInfo = new SellInfo(
				document.getElementById("sell-div"),
				openedAuctions = new OpenedAuctions(
					document.getElementById("openedAuctions-container"),
					document.getElementById("openedAuctionDetails-container"),
					document.getElementById("openedAuctionDetails"),
					document.getElementById("openedAuctionItems"),
					document.getElementById("openedAuctionBids"),
					document.getElementById("closeForm-id_auction"),
					document.getElementById("closeAuction-button"),
					document.getElementById("closeAuction-warning")
				),
				closedAuctions = new ClosedAuctions(
					document.getElementById("closedAuctions-container"),
					document.getElementById("closedAuctionDetails-container"),
					document.getElementById("closedAuctionDetails"),
					document.getElementById("closedAuctionItems"),
					document.getElementById("closedAuctionBuyer"),
					document.getElementById("closedAuction-warning")
				),
				document.getElementById("sellItem-container"),
				document.getElementById("sellItem-button"),
				document.getElementById("sellItem-warning"),
				document.getElementById("sellAuction-container"),
				document.getElementById("itemToSell-container"),
				document.getElementById("sellAuction-button"),
				document.getElementById("sellAuction-warning"),
				document.getElementById("closeAuction-button"),
				document.getElementById("closeAuction-warning")
			);
		};
		 
		this.refresh = function(){
			userInfo.show();
									
			if(cookieManager.getCookieView(userInfo.name) === "buy"){
				buyInfo.show();
				sell_div.style.display = 'none';
				buy_div.style.display = 'block';
			} else{
				sellInfo.show();
				buy_div.style.display = 'none';
				sell_div.style.display = 'block';
			}			
		};
		 
		buy_button.addEventListener("click", (e) => {
			buyInfo.show();
			sell_div.style.display = 'none';
			buy_div.style.display = 'block';
			cookieManager.setCookieView("buy", sessionStorage.getItem('username'));
		});
		 
		sell_button.addEventListener("click", (e) => {
			sellInfo.show();
			buy_div.style.display = 'none';
			sell_div.style.display = 'block';
			cookieManager.setCookieView("sell", sessionStorage.getItem('username'));
		});	 
	}
		 
	function UserInfo(_name, _idUser, usernameElements, idUserElements, _logout_button){
		this.name = _name;
		this.idUser = _idUser;
		this.logout_button = _logout_button;
		
		this.logout_button.addEventListener("click", (e) => {
			sessionStorage.clear();
		});

		this.show = function(){
			usernameElements.forEach(element => {
				element.textContent = this.name;
			});
			idUserElements.forEach(element => {
				element.textContent = this.idUser;
			});
		};
	}
	
	function CookiesManager(){
		var self = this;
		
		this.setCookieView = function(toDisplay, username){
			
			var expirationDate = new Date();
  			expirationDate.setMonth(expirationDate.getMonth() + 1);
  			
  			var cookieName = "toDisplay" + username + "=";
  			
  			var cookie = cookieName + toDisplay + "; expires=" + expirationDate.toUTCString() + ";";
			document.cookie = cookie.trim();
		}
		
		this.addVisitedAuction = function(id_auction, username){
			
			var expirationDate = new Date();
  			expirationDate.setMonth(expirationDate.getMonth() + 1);
  			
  			var cookieName = "visitedAuction" + username + "=";
  			var alreadyExist = false;
  			
			var cookies = document.cookie.split(";");
			for(var i = 0; i < cookies.length; i++){
				var cookie = cookies[i].trim();
				if(cookie.startsWith(cookieName)){
					var oldValue = cookie.substring(cookieName.length, cookie.length);
					alreadyExist = true;
					break;
				}
			}
			
			var value;
			
			if(alreadyExist){
				value = id_auction + "," + oldValue;
			} else{
				value = id_auction;
			}
			
			value = self.removeDuplicates(value);
			
			var cookie = cookieName + value + "; expires=" + expirationDate.toUTCString() + ";";
			document.cookie = cookie.trim();
			
		}
		
		this.getCookieView = function(username){
			var cookies = document.cookie.split(";");
			var cookieName = "toDisplay" + username + "=";
			
			for(var i = 0; i < cookies.length; i++){
				var cookie = cookies[i].trim();
				if(cookie.startsWith(cookieName)){
					var viewValue = cookie.substring(cookieName.length, cookie.length);
					return viewValue;
				}
			}
			return "buy";
		}
		
		this.getCookieVisitedAuction = function(username){
			var cookies = document.cookie.split(";");
			var cookieName = "visitedAuction" + username + "=";
			
			for(var i = 0; i < cookies.length; i++){
				var cookie = cookies[i].trim();
				if(cookie.startsWith(cookieName)){
					return cookie.substring(cookieName.length, cookie.length);
				}
			}
			
			return null;
		}
		
		this.removeDuplicates = function(string) {
			const numbers = string.split(',');
			const uniqueNumbers = [];
			const encounteredNumbers = {};
		
			for (let i = 0; i < numbers.length; i++) {
		    	const number = numbers[i];
		    	if (!encounteredNumbers[number]) {
		      		uniqueNumbers.push(number);
		      		encounteredNumbers[number] = true;
		   		}
		  	}
			const result = uniqueNumbers.join(',');
		
			return result;
		}
	}
	 
	function BuyInfo(_div, 
					 _auctions_container, 
					 _search_auction_button, 
					 _search_auction_warning, 
					 _searched_auctions_container, 
					 _auction_bids,
					 _last_visited_auctions){
		this.div = _div;
		this.auctions_container = _auctions_container;
		this.searchAuction_button = _search_auction_button;
		this.searchAuction_warning = _search_auction_warning;
		this.searchedAuctions_container = _searched_auctions_container;
		this.auctionBids = _auction_bids;
		this.lastVisitedAuctions = _last_visited_auctions;
		 
		var self = this;
		 
		this.searchAuction_button.addEventListener("click", (e) => {
			self.searchAuction_warning.style.display = 'none';
			cookieManager.setCookieView("buy", sessionStorage.getItem('username'));
			 
			var searchAuctions_form = e.target.closest("form");
			if(searchAuctions_form.checkValidity()){
				makeCall("POST", 'SearchByKeyword', searchAuctions_form, (req) => {
					switch(req.status){
						case 200:
							var auctionData = JSON.parse(req.responseText);
							var auctions = auctionData.auctions;
							self.searchedAuctions_container.innerHTML = '';
							self.auctionBids.clear();
							
							var div = document.createElement("div");
							div.textContent = "Here you can find what you are looking for:";
							self.searchedAuctions_container.appendChild(div);
							
							if(auctions.length > 0){
								var auction_info = document.createElement("div");
								self.searchedAuctions_container.appendChild(auction_info);
								
								var table = document.createElement("table");
								
								auctions.forEach(function(auction){
									var row = table.insertRow();
									var idCell = row.insertCell();
									var anchor = document.createElement("a");
									idCell.appendChild(anchor);
									
									anchor.textContent = auction.id_auction;
									anchor.setAttribute('id', auction.id_auction);
									anchor.addEventListener('click', (e) => {
										console.log("Click asta");
										cookieManager.addVisitedAuction(e.target.getAttribute("id"), sessionStorage.getItem('username'));
										visitedAuctionsInfo.show();
										self.auctionBids.show(e.target.getAttribute("id"));
									}, false);
									anchor.href="#";
									
									row.appendChild(idCell);
									table.appendChild(row);
								});
								
								auction_info.appendChild(table);
							} else{
								self.searchedAuctions_container.textContent = '';
								self.searchAuction_warning.textContent = "No auction available!";
								self.searchAuction_warning.style.display = 'block';
							}
							break;
						case 400:
						case 401:
						case 500:
							self.searchAuction_warning.textContent = req.responseText;
							self.searchAuction_warning.style.display = 'block';
							break;
						default:
							self.searchAuction_warning.textContent = "Error status:" + req.status;
                        	self.searchAuction_warning.style.display = 'block';
					}
				}); 
			}else{
				searchAuctions_form.reportValidity();
			}	 
		});
		
		this.show = function(){
			self.auctionBids.hide();
			self.lastVisitedAuctions.show();
			
			makeCall("GET", 'GetAwardedAuctions', null, (req) =>{
				 switch(req.status){
					 case 200: //ok
					 	var auctionData = JSON.parse(req.responseText);
					 	var auctions = auctionData.auctions;
					 	self.auctions_container.innerHTML = '';
					 	
				 		if(auctions.length > 0){
						 	auctions.forEach(function(auction) {
							 	var auction_info = document.createElement("div");
							 	auction_info.textContent = "Id asta: " + auction.id_auction + " Price: " + auction.price;
							 	self.auctions_container.appendChild(auction_info);
								
							 	var table = document.createElement("table");
								var headerRow = table.insertRow();
								var idHeader = document.createElement("th");
                            	idHeader.textContent = "Id";
                            	idHeader.classList.add("cell");
                            	headerRow.appendChild(idHeader);
                            	var nameHeader = document.createElement("th");
                            	nameHeader.textContent = "Name";
                            	nameHeader.classList.add("cell");
                            	headerRow.appendChild(nameHeader);
                            	var descriptionHeader = document.createElement("th");
                            	descriptionHeader.textContent = "Description";
                            	descriptionHeader.classList.add("cell");
                            	headerRow.appendChild(descriptionHeader);
                            	var imgHeader = document.createElement("th");
                            	imgHeader.classList.add("cell");
                            	imgHeader.textContent = "Image";
                            	headerRow.appendChild(imgHeader);
								
								var items = auction.items;
								
							 	items.forEach(function(item) {
							 		var row = table.insertRow();
									var idCell = row.insertCell();
									idCell.textContent = item.id_item;
									idCell.classList.add("cell");
									var nameCell = row.insertCell();
									nameCell.textContent = item.name;
									nameCell.classList.add("cell");
									var descriptionCell = row.insertCell();
									descriptionCell.textContent = item.description;
									descriptionCell.classList.add("cell");
									var imgCell = row.insertCell();
									imgCell.classList.add("cell");
									var imgTag = document.createElement("img");
									imgTag.classList.add("image");
									imgTag.src = imgSrc + item.img_name;
									imgCell.appendChild(imgTag);
						 		});
						 		
						 		self.auctions_container.appendChild(table);
						 	});
						} else {
							self.auctions_container.textContent = "You currently haven't awarded any auction";
						}
						break;
					case 400:
					case 401:
					case 500:
						self.auctions_container.textContent = req.responseText;
						break;
					default:
						self.auctions_container.textContent = "Error status: " + req.status;
						break;
				}
			});
		}
	}
	 
	function SellInfo(_div, 
					  _opened_auctions, 
					  _closed_auctions, 
					  _sellItem_container, 
					  _sellItem_button, 
					  _sellItem_warning, 
					  _sellAuction_container, 
					  _itemToSell_container, 
					  _sellAuction_button, 
					  _sellAuction_warning,
					  _close_auction_button,
					  _close_auction_warning){
		this.div = _div;
		this.opened_auctions = _opened_auctions;
		this.closed_auctions = _closed_auctions;
		this.sellItem_container = _sellItem_container;
		this.sellItem_button = _sellItem_button;
		this.sellItem_warning = _sellItem_warning;
		this.sellAuction_container = _sellAuction_container;
		this.itemToSell_container = _itemToSell_container;
		this.sellAuction_button = _sellAuction_button;
		this.sellAuction_warning = _sellAuction_warning;
		this.close_auction_button = _close_auction_button;
		this.close_auction_warning = _close_auction_warning;
	
		var self = this;
		
		this.sellItem_button.addEventListener("click", (e) => {
			var sellItem_form = e.target.closest("form");
			cookieManager.setCookieView("sell", sessionStorage.getItem('username'));
			
			if(sellItem_form.checkValidity()){
				makeCall("POST", 'CreateItem', sellItem_form, (req) => {
					switch(req.status){
						case 200:
							self.sellItem_warning.style.display = 'none';
							self.showItemsToSell();
							break;
						case 400:
						case 401:
						case 500:
							self.sellItem_warning.textContent = req.responseText;
							self.sellItem_warning.style.display = 'block';
							break;
						default:
							self.sellItem_warning.textContent = req.responseText;
							self.sellItem_warning.style.display = 'block';
							break;
					}
				});
			}else{
				sellItem_form.reportValidity();
			}
		});
		
		this.sellAuction_button.addEventListener("click", (e) => {
			var sellAuction_form = e.target.closest("form");
			cookieManager.setCookieView("sell", sessionStorage.getItem('username'));
			
			if(sellAuction_form.checkValidity()){
				makeCall("POST", 'CreateAuction', sellAuction_form, (req) => {
					switch(req.status){
						case 200:
							self.show();
							break;
						case 400:
						case 401:
						case 500:
							self.sellAuction_warning.textContent = req.responseText;
							self.sellAuction_warning.style.display = 'block';
							break;
						default:
							self.sellAuction_warning.textContent = req.responseText;
							self.sellAuction_warning.style.display = 'block';
							break;
					}
				})
			}else{
				sellAuction_form.reportValidity();
			}
		});
		
		this.show = function(){
			self.opened_auctions.show();
			self.closed_auctions.show();
			self.showItemsToSell();
		}
		
		this.showItemsToSell = function(){
			makeCall("GET", 'GetNotAuctionedItems', null, (req) => {
				switch(req.status){
					case 200:
						var auctionData = JSON.parse(req.responseText);
						var items = auctionData.items;
						self.itemToSell_container.innerHTML = '';
						
						var container = self.itemToSell_container;
						
						if(items.length > 0){
							items.forEach(function(item){
								var divItem = document.createElement("div");
								divItem.classList.add("form-part");
								var label = document.createElement("label");
								label.htmlFor = "item";
								label.textContent = item.name;
								divItem.appendChild(label);
								var checkbox = document.createElement("input");
								checkbox.type = "checkbox";
								checkbox.id="item";
								checkbox.value = item.id_item;
								checkbox.name = "ids_item";
								divItem.appendChild(checkbox);

    							container.appendChild(divItem);
							});
						}else{
							this.itemToSell_container.textContent = "You don't have any item to sell";
						}
						break;
					case 400:
					case 401:
					case 500:
						self.itemToSell_container.textContent = req.responseText;
						break;
					default:
						self.itemToSell_container.textContent = "Error status: " + req.status;
						break;	
				}
			})
		}
		
		this.close_auction_button.addEventListener("click", (e) => {
			var close_form = e.target.closest("form");
			cookieManager.setCookieView("sell", sessionStorage.getItem('username'));
			
			makeCall("POST", "CloseAuction", close_form, (req) => {
				switch(req.status){
					case 200:
						self.show();
						break;
					case 400:
					case 401:
					case 500:
						self.close_auction_warning.textContent = req.responseText;
						self.close_auction_warning.style.display = 'block';
						break;
					default:
						self.close_auction_warning.textContent = "Error status: " + req.status;
						self.close_auction_warning.style.display = 'block';
						break;
				}
			});
		});
		
	}
	
	function AuctionBids(_auction_bids_container, 
						 _auction_items_container, 
						 _current_bids_container, 
						 _offer_form_container, 
						 _min_offer_span, 
						 _offer_form_id_auction, 
						 _place_bid_button, 
						 _place_bid_warning,
						 _auction_bids_warning){
							 
		this.auction_bids_container = _auction_bids_container;
		this.auction_items_container = _auction_items_container;
		this.current_bids_container = _current_bids_container;
		this.offer_form_container = _offer_form_container;
		this.min_offer = _min_offer_span;
		this.offer_form_id_auction = _offer_form_id_auction;
		this.place_bid_button = _place_bid_button;
		this.place_bid_warning = _place_bid_warning;
		this.auction_bids_warning = _auction_bids_warning;
		
		var self = this;
		
		this.show = function(id_auction){
			self.auction_bids_container.style.display = 'block';
			self.auction_bids_warning.style.display = 'none';
			self.offer_form_id_auction.value = id_auction;
			
			self.auction_items_container.innerHTML = '';
			self.showBidsInfo(id_auction);
		}
		
		this.hide = function(){
			self.auction_bids_container.style.display = 'none';
			self.auction_bids_warning.style.display = 'none';
		}
		
		this.showBidsInfo = function(id_auction){
			self.auction_items_container.style.display = 'none';
			self.current_bids_container.style.display = 'none';
			
			makeCall("GET", 'GetAuctionBids?id_auction=' + id_auction, null, (req) => {
				switch(req.status){
					case 200:
						var bidData = JSON.parse(req.responseText);
						var items = bidData.items;
						var bids = bidData.bids;
						var minOffer = bidData.minOffer;
						
						self.auction_items_container.innerHTML = '';
						var divItems = document.createElement("div");
						divItems.classList.add("bids-title");
						divItems.textContent = "The auction sells these articles";
						self.auction_items_container.appendChild(divItems);
						
						if(items.length > 0){
							var table = document.createElement("table");
							
							var headerRow = table.insertRow();
							var nameHeader = document.createElement("th");
							nameHeader.classList.add("cell");
                           	nameHeader.textContent = "Name";
                           	headerRow.appendChild(nameHeader);
                           	var descriptionHeader = document.createElement("th");
                           	descriptionHeader.classList.add("cell");
                           	descriptionHeader.textContent = "Description";
                           	headerRow.appendChild(descriptionHeader);
                           	var priceHeader = document.createElement("th");
                           	priceHeader.classList.add("cell");
                           	priceHeader.textContent = "Price";
                           	headerRow.appendChild(priceHeader);
                           	var imageHeader = document.createElement("th");
                           	imageHeader.classList.add("cell");
                           	imageHeader.textContent = "Image";
                           	headerRow.appendChild(imageHeader);
                           	
                           	items.forEach(function(item){
								var row = table.insertRow();
								var nameCell = row.insertCell();
								nameCell.classList.add("cell");
								nameCell.textContent = item.name;
								var descriptionCell = row.insertCell();
								descriptionCell.classList.add("cell");
								descriptionCell.textContent = item.description;
								var priceCell = row.insertCell();
								priceCell.classList.add("cell");
								priceCell.textContent = item.price;
								var imgCell = row.insertCell();
								var imgTag = document.createElement("img");
								imgTag.classList.add("image");
								imgTag.src = imgSrc + item.img_name;
								imgCell.classList.add("cell");
								imgCell.appendChild(imgTag);
							});
							
							self.auction_items_container.appendChild(table);
							
						} else{
							self.auction_bids_container.textContent = "This auction does not contain any item";
						}

						self.current_bids_container.innerHTML = '';
						var divBids = document.createElement("div");
						divBids.classList.add("bids-title");
						divBids.textContent = "Current Bids";
						
						self.auction_items_container.appendChild(divBids);
						
						if(bids.length > 0){
							var table = document.createElement("table");
							
							var headerRow = table.insertRow();
							var idHeader = document.createElement("th");
							idHeader.classList.add("cell");
							idHeader.textContent = "Id user";
							headerRow.appendChild(idHeader);
							var priceHeader = document.createElement("th");
							priceHeader.classList.add("cell");
							priceHeader.textContent = "Price";
							headerRow.appendChild(priceHeader);
							var dateHeader = document.createElement("th");
							dateHeader.classList.add("cell");
							dateHeader.textContent = "Date";
							headerRow.appendChild(dateHeader);
							
							bids.forEach(function(bid){
								var row = table.insertRow();
								var idCell = row.insertCell();
								idCell.classList.add("cell");
								idCell.textContent = bid.id_user;
								var priceCell = row.insertCell();
								priceCell.classList.add("cell");
								priceCell.textContent = bid.price;
								var dateCell = row.insertCell();
								dateCell.classList.add("cell");
								dateCell.textContent = bid.date;
							})
							
							self.current_bids_container.appendChild(table);
							
						} else{
							self.current_bids_container.textContent = "There are no current bids!";
						}
						
						self.min_offer.textContent = "Minimum bid is " + minOffer;
						
						self.auction_items_container.style.display = 'block';
						self.current_bids_container.style.display = 'block';
						break;
					case 400:
					case 401:
					case 500:
						self.auction_items_container.style.display = 'none';
						self.current_bids_container.style.display = 'none';
						self.offer_form_container.style.display = 'none';
						self.auction_bids_warning.textContent = req.responseText;
						self.auction_bids_warning.style.display = 'block';
						break;
					default:
						self.auction_items_container.style.display = 'none';
						self.current_bids_container.style.display = 'none';
						self.auction_bids_warning.textContent = "Error status: " + req.status;
						self.auction_bids_warning.style.display = 'block';
						break;
				}
			});
		}
		
		this.clear = function(){
			self.auction_items_container.innerHTML = '';
			self.current_bids_container.innerHTML = '';
			self.min_offer.innerHTML = '';
			self.auction_bids_container.style.display = 'none';
		}
		
		this.place_bid_button.addEventListener("click", (e) => {
			self.place_bid_warning.style.display = 'none';
			self.place_bid_warning.innerHTML = '';
			cookieManager.setCookieView("buy", sessionStorage.getItem('username'));
			
			var offer_form = e.target.closest("form");
			var id_auction = self.offer_form_id_auction.value;
			
			makeCall("POST", 'RegisterBid', offer_form, (req) => {
				switch(req.status){
					case 200:
						self.show(id_auction);
						break;
					case 400:
					case 401:
					case 500:
						self.place_bid_warning.textContent = req.responseText;
						self.place_bid_warning.style.display = 'block';
						break;
					default:
						self.place_bid_warning.textContent = req.responseText;
						self.place_bid_warning.style.display = 'block';
						break;
				}
			});
		});
	}
	
	function OpenedAuctions(_auctions_container, 
							_auction_container, 
							_opened_auction_details,
							_opened_auction_items,
							_opened_auction_bids,
							_close_form_id_auction,
							_close_auction_button, 
							_close_auction_warning){
								
		this.opened_auctions_container = _auctions_container;
		this.auction_container = _auction_container;
		this.opened_auction_details = _opened_auction_details;
		this.opened_auction_items = _opened_auction_items;
		this.opened_auction_bids = _opened_auction_bids;
		this.close_form_id_auction = _close_form_id_auction;
		this.close_auction_button = _close_auction_button;
		this.close_auction_warning = _close_auction_warning;
		
		var self = this;
		
		this.show = function(){
			self.close_auction_warning.style.display = 'none';
			
			makeCall("GET", 'GetOpenedAuctions', null, (req) => {
				switch(req.status){
					case 200:
						var auctionData = JSON.parse(req.responseText);
						var auctions = auctionData.auctions;
						self.opened_auctions_container.innerHTML = '';
						self.auction_container.style.display = 'none';
						
						if(auctions.length > 0){
							var table = document.createElement("table");
							
							var headerRow = table.insertRow();
							var idHeader = document.createElement("th");
                           	idHeader.textContent = "Id_auction";
                           	idHeader.classList.add("cell");
                           	headerRow.appendChild(idHeader);
                           	var remainingHeader = document.createElement("th");
                           	remainingHeader.textContent = "Remaining";
                           	remainingHeader.classList.add("cell");
                           	headerRow.appendChild(remainingHeader);
                           	var dateHeader = document.createElement("th");
                           	dateHeader.textContent = "Closing date";
                           	dateHeader.classList.add("cell");
                           	headerRow.appendChild(dateHeader);
                           	
							auctions.forEach(function(auction) {
								var row = table.insertRow();
								var idCell = row.insertCell();
								idCell.classList.add("cell");
								
								var anchor = document.createElement("a");
								idCell.appendChild(anchor);
								
								anchor.textContent = auction.id_auction;
								anchor.setAttribute('id', auction.id_auction);
								anchor.addEventListener('click', (e) => {
									self.showOpenedAuctionDetails(e.target.getAttribute("id"));
								}, false);
								anchor.href="#";
								
								var currentTime = new Date();
								
								var diffMillisec = Math.abs(currentTime.getTime() - auction.millisec);
								var days = Math.floor(diffMillisec / (1000 * 60 * 60 * 24));
								var hours = Math.floor((diffMillisec % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
								
								var remainingCell = row.insertCell();
								remainingCell.textContent = days + " days and " + hours + " hours.";
								remainingCell.classList.add("cell");
								
								var dateCell = row.insertCell();
								dateCell.textContent = auction.closing_date;
								dateCell.classList.add("cell");
						 	});
						 	self.opened_auctions_container.appendChild(table);
						} else{
							self.opened_auctions_container.textContent = "You haven't opened any auction yet.";
						}
						break;
					case 400:
					case 401:
					case 500:
						self.opened_auctions_container.textContent = req.responseText;
						break;
					default:
						self.opened_auctions_container.textContent = "Error status: " + req.status;
						break;
				} 
			});
		}
		
		this.showOpenedAuctionDetails = function(id_auction){
			self.close_auction_warning.style.display = 'none';
			
			makeCall("GET", 'GetAuctionDetails?id_auction='+id_auction, null, (req) =>{
				switch(req.status){
					case 200:
						var detailsData = JSON.parse(req.responseText);
						var auctionDetails = detailsData.auction;
						var bids = detailsData.bids;
						var items = detailsData.items;
						
						self.opened_auction_details.innerHTML = '';
						self.showDetails(auctionDetails);
						
						self.opened_auction_items.innerHTML = '';
						self.showItems(items);
						
						self.opened_auction_bids.innerHTML = '';
						self.showBids(bids);
						
						self.close_form_id_auction.value = id_auction;
						
						self.auction_container.style.display = 'block';		
                       	break;
					case 400:
					case 401:
					case 500:
						self.opened_auction_details.innerHTML = '';
						self.opened_auction_items.innerHTML = '';
						self.opened_auction_bids.innerHTML = '';
						self.close_auction_warning.textContent = req.responseText;
						self.close_auction_warning.style.display = 'block';
						break;
					default:
						self.opened_auction_details.innerHTML = '';
						self.opened_auction_items.innerHTML = '';
						self.opened_auction_bids.innerHTML = '';
						self.close_auction_warning.textContent = "Error status: " + req.status;
						self.close_auction_warning.style.display = 'block';
						break;
				}
			});
		}
		
		this.showDetails = function(auction){
			var divDetails = document.createElement("div");
			divDetails.classList.add("detail-title");
			var bold = document.createElement("b");
			bold.textContent = "Auction details";
			divDetails.appendChild(bold);
			self.opened_auction_details.appendChild(divDetails);
			
			var detailsTable = document.createElement("table");
			
			var headerRow = detailsTable.insertRow();
			var idHeader = document.createElement("th");
           	idHeader.textContent = "Id_auction";
           	idHeader.classList.add("cell");
           	headerRow.appendChild(idHeader);
           	var startingHeader = document.createElement("th");
           	startingHeader.textContent = "Starting Bid";
           	startingHeader.classList.add("cell");
           	headerRow.appendChild(startingHeader);
           	var incrementHeader = document.createElement("th");
           	incrementHeader.textContent = "Bid increment";
           	incrementHeader.classList.add("cell");
           	headerRow.appendChild(incrementHeader);
           	var dateHeader = document.createElement("th");
           	dateHeader.textContent = "Closing date";
           	dateHeader.classList.add("cell");
           	headerRow.appendChild(dateHeader);
           	var stateHeader = document.createElement("th");
           	stateHeader.textContent = "Auction state";
           	stateHeader.classList.add("cell");
           	headerRow.appendChild(stateHeader);
                           	
			var row = detailsTable.insertRow();
			var idCell = row.insertCell();
			idCell.textContent = auction.id_auction;
			idCell.classList.add("cell");
			var bidCell = row.insertCell();
			bidCell.textContent = auction.starting_bid;
			bidCell.classList.add("cell");
			var incrementCell = row.insertCell();
			incrementCell.textContent = auction.bid_increment;
			incrementCell.classList.add("cell");
			var dateCell = row.insertCell();
			dateCell.textContent = auction.closing_date;
			dateCell.classList.add("cell");
			var stateCell = row.insertCell();
			stateCell.textContent = auction.auction_state;
			stateCell.classList.add("cell");
			
			self.opened_auction_details.appendChild(detailsTable);
		}
		
		this.showBids = function(bids){
			var divBids = document.createElement("div");
			divBids.classList.add("detail-title");
			var bold = document.createElement("b");
			bold.textContent = "Bid summary";
			divBids.appendChild(bold);
			self.opened_auction_bids.appendChild(divBids);
			
			if(bids.length > 0){
				var detailsTable = document.createElement("table");
				
				var headerRow = detailsTable.insertRow();
				var idHeader = document.createElement("th");
               	idHeader.textContent = "Id_user";
               	idHeader.classList.add("cell");
               	headerRow.appendChild(idHeader);
               	var priceHeader = document.createElement("th");
               	priceHeader.textContent = "price";
               	priceHeader.classList.add("cell");
               	headerRow.appendChild(priceHeader);
               	var dateHeader = document.createElement("th");
               	dateHeader.textContent = "date";
               	dateHeader.classList.add("cell");
               	headerRow.appendChild(dateHeader);
               	
               	bids.forEach(function(bid){
					var row = detailsTable.insertRow();
               		var idCell = row.insertCell();
					idCell.textContent = bid.id_user;
					idCell.classList.add("cell");
					var priceCell = row.insertCell();
					priceCell.textContent = bid.price;
					priceCell.classList.add("cell");
					var dateCell = row.insertCell();
					dateCell.textContent = bid.date;
					dateCell.classList.add("cell");
				});
				
				self.opened_auction_bids.appendChild(detailsTable);
				
			}else{
				var divText = document.createElement("div");
				divText.textContent = "Your auction doesn't have any bid yet.";
				self.opened_auction_bids.appendChild(divText);
			}
		}
		
		this.showItems = function(items){
			var divItems = document.createElement("div");
			divItems.classList.add("detail-title");
			var bold = document.createElement("b");
			bold.textContent = "Item summary";
			divItems.appendChild(bold);
			self.opened_auction_items.appendChild(divItems);
			
			if(items.length > 0){
				var detailsTable = document.createElement("table");
				
				var headerRow = detailsTable.insertRow();
				var idHeader = document.createElement("th");
               	idHeader.textContent = "Id item";
               	idHeader.classList.add("cell");
               	headerRow.appendChild(idHeader);
               	var nameHeader = document.createElement("th");
               	nameHeader.textContent = "Name";
               	nameHeader.classList.add("cell");
               	headerRow.appendChild(nameHeader);
               	
               	items.forEach(function(item){
					var row = detailsTable.insertRow();
               		var idCell = row.insertCell();
					idCell.textContent = item.id_item;
					idCell.classList.add("cell");
					var nameCell = row.insertCell();
					nameCell.textContent = item.name;
					nameCell.classList.add("cell");
				});
				
				self.opened_auction_items.appendChild(detailsTable);
				
			}else{
				var divText = document.createElement("div");
				divText.textContent = "This auction is empty.";
				self.opened_auction_items.appendChild(divText);
			}
		}
	}
	
	function ClosedAuctions(_auctions_container, 
							_auction_container, 
							_auction_details,
							_auction_items,
							_auction_buyer,
							_auction_warning){
		this.auctions_container = _auctions_container;
		this.auction_container = _auction_container;
		this.auction_details = _auction_details;
		this.auction_items = _auction_items;
		this.auction_buyer = _auction_buyer;
		this.auction_warning = _auction_warning;
		
		var self = this;
		
		this.show = function(){
			self.auction_warning.style.display = 'none';
			
			makeCall("GET", 'GetClosedAuctions', null, (req) => {
				switch(req.status){
					case 200:
						var auctionData = JSON.parse(req.responseText);
						var auctions = auctionData.auctions;
						self.auctions_container.innerHTML = '';
						
						if(auctions.length > 0){
							var table = document.createElement("table");
							
							var headerRow = table.insertRow();
							var idHeader = document.createElement("th");
                           	idHeader.textContent = "Id_auction";
                           	idHeader.classList.add("cell");
                           	headerRow.appendChild(idHeader);
                           	var dateHeader = document.createElement("th");
                           	dateHeader.textContent = "Closing date";
                           	dateHeader.classList.add("cell");
                           	headerRow.appendChild(dateHeader);
                           	
							auctions.forEach(function(auction) {
								var row = table.insertRow();
								var idCell = row.insertCell();
								idCell.classList.add("cell");
								
								var anchor = document.createElement("a");
								idCell.appendChild(anchor);
								
								anchor.textContent = auction.id_auction;
								anchor.setAttribute('id', auction.id_auction);
								anchor.addEventListener('click', (e) => {
									self.showClosedAuctionDetails(e.target.getAttribute("id"));
								}, false);
								anchor.href="#";
								
								var dateCell = row.insertCell();
								dateCell.textContent = auction.closing_date;
								dateCell.classList.add("cell");
						 	});
						 	
						 	self.auctions_container.appendChild(table);
							
						}else{
							self.auctions_container.textContent = "You haven't closed any auction yet.";
						}
						break;
					case 400:
					case 401:
					case 500:
						self.auctions_container.textContent = req.responseText;
						break;
					default:
						self.auctions_container.textContent = "Error status: " + req.status;
						break;
				} 
			});
		}
		
		this.showClosedAuctionDetails = function(id_auction){
			self.auction_warning.style.display = 'none';
			
			makeCall("GET", 'GetAuctionDetails?id_auction='+id_auction, null, (req) =>{
				switch(req.status){
					case 200:
						var detailsData = JSON.parse(req.responseText);
						var auctionDetails = detailsData.auction;
						var items = detailsData.items;
						var buyer = detailsData.buyer;
						
						self.auction_details.innerHTML = '';
						self.showDetails(auctionDetails);
						
						self.auction_items.innerHTML = '';
						if(buyer !== undefined){
							self.showItems(items);
						}
												
						self.auction_buyer.innerHTML = '';
						self.showBuyer(buyer);
						
						self.auction_container.style.display = 'block';		
                       	break;
					case 400:
					case 401:
					case 500:
						self.auction_details.innerHTML = '';
						self.auction_items.innerHTML = '';
						self.auction_buyer.innerHTML = '';
						self.auction_warning.textContent = req.responseText;
						self.auction_warning.style.display = 'block';
						break;
					default:
						self.auction_details.innerHTML = '';
						self.auction_items.innerHTML = '';
						self.auction_buyer.innerHTML = '';
						self.auction_warning.textContent = "Error status: " + req.status;
						self.auction_warning.style.display = 'block';
						break;
				}
			});
		}
		
		this.showDetails = function(auction){
			var divDetails = document.createElement("div");
			divDetails.classList.add("detail-title");
			var bold = document.createElement("b");
			bold.textContent = "Auction details";
			divDetails.appendChild(bold);
			self.auction_details.appendChild(divDetails);
			
			var detailsTable = document.createElement("table");
			
			var headerRow = detailsTable.insertRow();
			var idHeader = document.createElement("th");
           	idHeader.textContent = "Id_auction";
           	idHeader.classList.add("cell");
           	headerRow.appendChild(idHeader);
           	var startingHeader = document.createElement("th");
           	startingHeader.textContent = "Starting Bid";
           	startingHeader.classList.add("cell");
           	headerRow.appendChild(startingHeader);
           	var incrementHeader = document.createElement("th");
           	incrementHeader.textContent = "Bid increment";
           	incrementHeader.classList.add("cell");
           	headerRow.appendChild(incrementHeader);
           	var dateHeader = document.createElement("th");
           	dateHeader.textContent = "Closing date";
           	dateHeader.classList.add("cell");
           	headerRow.appendChild(dateHeader);
           	var stateHeader = document.createElement("th");
           	stateHeader.textContent = "Auction state";
           	stateHeader.classList.add("cell");
           	headerRow.appendChild(stateHeader);
                           	
			var row = detailsTable.insertRow();
			var idCell = row.insertCell();
			idCell.textContent = auction.id_auction;
			idCell.classList.add("cell");
			var bidCell = row.insertCell();
			bidCell.textContent = auction.starting_bid;
			bidCell.classList.add("cell");
			var incrementCell = row.insertCell();
			incrementCell.textContent = auction.bid_increment;
			incrementCell.classList.add("cell");
			var dateCell = row.insertCell();
			dateCell.textContent = auction.closing_date;
			dateCell.classList.add("cell");
			var stateCell = row.insertCell();
			stateCell.textContent = auction.auction_state;
			stateCell.classList.add("cell");
			
			self.auction_details.appendChild(detailsTable);
		}
		
		this.showBuyer = function(buyer){
			var divDetails = document.createElement("div");
			divDetails.classList.add("detail-title");
			var bold = document.createElement("b");
			bold.textContent = "Buyer info";
			divDetails.appendChild(bold);
			self.auction_buyer.appendChild(divDetails);
			
			if(buyer !== undefined){
				var detailsTable = document.createElement("table");
			
				var headerRow = detailsTable.insertRow();
				var idHeader = document.createElement("th");
	           	idHeader.textContent = "Id";
	           	idHeader.classList.add("cell");
	           	headerRow.appendChild(idHeader);
	           	var userHeader = document.createElement("th");
	           	userHeader.textContent = "Username";
	           	userHeader.classList.add("cell");
	          	headerRow.appendChild(userHeader);
	          	var addressHeader = document.createElement("th");
	           	addressHeader.textContent = "Address";
	           	addressHeader.classList.add("cell");
	          	headerRow.appendChild(addressHeader);
	                           	
				var row = detailsTable.insertRow();
				var idCell = row.insertCell();
				idCell.textContent = buyer.id_user;
				idCell.classList.add("cell");
				var userCell = row.insertCell();
				userCell.textContent = buyer.username;
				userCell.classList.add("cell");
				var addressCell = row.insertCell();
				addressCell.textContent = buyer.address;
				addressCell.classList.add("cell");
				
				self.auction_buyer.appendChild(detailsTable);
			} else{
				var divText = document.createElement("div");
				divText.textContent = "This auction has not been sold!";
				self.auction_buyer.appendChild(divText);
			}
		}
		
		this.showItems = function(items){
			var divItems = document.createElement("div");
			divItems.classList.add("detail-title");
			var bold = document.createElement("b");
			bold.textContent = "Item summary";
			divItems.appendChild(bold);
			self.auction_items.appendChild(divItems);
			
			if(items.length > 0){
				var detailsTable = document.createElement("table");
				
				var headerRow = detailsTable.insertRow();
				var idHeader = document.createElement("th");
               	idHeader.textContent = "Id item";
               	idHeader.classList.add("cell");
               	headerRow.appendChild(idHeader);
               	var nameHeader = document.createElement("th");
               	nameHeader.textContent = "Name";
               	nameHeader.classList.add("cell");
               	headerRow.appendChild(nameHeader);
               	
               	items.forEach(function(item){
					var row = detailsTable.insertRow();
               		var idCell = row.insertCell();
					idCell.textContent = item.id_item;
					idCell.classList.add("cell");
					var nameCell = row.insertCell();
					nameCell.textContent = item.name;
					nameCell.classList.add("cell");
				});
				
				self.auction_items.appendChild(detailsTable);
				
			}else{
				var divText = document.createElement("div");
				divText.textContent = "This auction is empty.";
				self.auction_items.appendChild(divText);
			}
		}
	}
	
	function VisitedAuctionsInfo(_last_visited_auctions_id_container, _last_visited_auctions_warning, _fast_auction_bids){
		this.lastVisitedAuctionsIdContainer = _last_visited_auctions_id_container;
		this.lastVisitedWarning = _last_visited_auctions_warning;
		this.fastAuctionBids = _fast_auction_bids;
		
		var self = this;
		
		this.show = function(){
			self.fastAuctionBids.hide();
			
			var auction_ids = cookieManager.getCookieVisitedAuction(sessionStorage.getItem('username'));
			self.lastVisitedWarning.innerHTML = '';
			self.lastVisitedWarning.style.display = 'none';
			
			if(auction_ids !== null){
				makeCallString("POST", 'GetStillOpenedAuctions', auction_ids, (req) =>{
					switch(req.status){
						case 200:
							var new_ids = req.responseText;
							var ids_auction = new_ids.split(",");

							if(ids_auction.length > 1){
								var table = document.createElement("table");
								
								ids_auction.forEach(function(id){
									var row = table.insertRow();
									var idCell = row.insertCell();
									var anchor = document.createElement("a");
									idCell.appendChild(anchor);
									
									anchor.textContent = id;
									anchor.setAttribute('id', id);
									anchor.addEventListener('click', (e) => {
										self.fastAuctionBids.show(e.target.getAttribute("id"));
									}, false);
									anchor.href="#";
									
									row.appendChild(idCell);
									table.appendChild(row);
								});
								
								self.lastVisitedAuctionsIdContainer.innerHTML = '';
								self.lastVisitedAuctionsIdContainer.appendChild(table);
							} else{
								console.log("Non ho da mostrare aste");
								self.lastVisitedAuctionsIdContainer.textContent = "No auctions to display";
							}
							break;
						case 400:
						case 401:
						case 500:
							self.lastVisitedWarning.textContent = req.responseText;
							self.lastVisitedWarning.style.display = 'block';
							break;
						default:
							self.lastVisitedWarning.textContent = "Error status: " + req.status;
							self.lastVisitedWarning.style.display = 'block';
							break;
					}
				})
			} else {
				self.lastVisitedAuctionsIdContainer.textContent = "No auctions to display";
			}
		}	
	}
})();