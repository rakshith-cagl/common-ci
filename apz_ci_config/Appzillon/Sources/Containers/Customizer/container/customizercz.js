Apz.Customizer.prototype.fnDesignerInitialise = function() {
    $('#CMZR01__Customizer__editBtn').removeClass('sno');
    $('#CMZR01__Customizer__saveBtn,#CMZR01__Customizer__cancelBtn').addClass('sno');
    $('#CMZR01__Customizer__applyBtn').addClass('sno');
    this.fnDesignerOnchangeDD('DG');
};
Apz.Customizer.prototype.fnEditorInitialise = function() {
    this.fnEditorSetDroppable();
};
Apz.Customizer.prototype.fnEditorPostInit = function() {
    setTimeout(function() {
        for (var i = 0; i < apz.customizer.sContainerData.length; i++) {
            if ($('#' + apz.customizer.sContainerData[0].ContainerId).length !== 0) {
                $('#' + apz.customizer.sContainerData[i].ContainerId).children().addClass('AppRow');
            }
        }
        $('#CMZR01__Customizer__MicroAppsList li').addClass('MicroAppsList');
        $('#CMZR01__Customizer__CallFormsList li').addClass('CallFormsList');
        $('#CMZR01__Customizer__NavigatorsList li').addClass('NavigatorsList');
        if (apz.customizer.isEditMode) {
            var lCloseIcon =
                '<button enabled="enabled" class="ett-bttn tsp min icl icon-close" onclick="apz.customizer.fnDesignerCloseApp(this)"  style="position:absolute" type="button"><svg class="ett-icon icon-remove px20 " aria-hidden="true" id="btn_icon_icon-remove"><use xlink:href="#icon-remove"/></svg><span></span></button>';
            $(".AppRow").append(lCloseIcon);
        }
    }, 10);
};
Apz.Customizer.prototype.fnDesignerLaunch = function(params) {
    var lScreen = this.getCurrentScreen();
    var lTempid = $('.currentTemplate').children()[0].id.split('_');
    lTempid = lTempid[lTempid.length - 1];
    var appId =  this.getselectedApp();
    this.apz.currAppId = appId;
    if (params.action == "cancel") {
        lScreen = this.currScr;
        var lTempLaunch = this.currDesign;
    } else if (params.action == "apply") {
        var lTempLaunch = lTempid;
    }
    $('.currentTemplate').removeClass('currentTemplate');
    $('#CMZR01__Customizer__Carousel__' + lTempLaunch).parent().addClass('currentTemplate');
    var lParams = {
        'appId': appId,
        'scr': lScreen,
        'layout': this.apz.getLayoutForGroup({
            'appId': appId,
            'scr': lScreen,
            'deviceGroup': this.currDeviceGroup,
            'orientation': this.apz.orientation
        }),
        'template': lTempLaunch,
        'userObj': {
            'scr': lScreen,
            'template': lTempLaunch
        }
    };
    this.apz.launchScreen(lParams);
};
Apz.Customizer.prototype.fnDesignerDragcheck = function(pObj) {
    var lScreen = this.getCurrentScreen();
    $('.designerLauncher .microAppName,.designerLauncher .navigatorName').css('cursor', 'crosshair');
    $(pObj).parent().draggable('enable');
    var lAppId = $(pObj).find('.appId').text();
    if ($('#' + this.apz.currAppId + '__' + lScreen + '__' + lAppId).css('display') !== 'none') {
        $(pObj).css('cursor', 'not-allowed');
        $('.parentContainer').find(pObj).parent().draggable('disable');
    } else {
        $(pObj).css('cursor', 'grab');
    }
};
Apz.Customizer.prototype.fnDesignerRender = function() {
    var lScreen = this.getCurrentScreen();
    var lTempid = $('.currentTemplate').children()[0].id.split('_');
    lTempid = lTempid[lTempid.length - 1];
    var appId = this.getselectedApp();
    this.apz.currAppId = appId;
    var layout = this.apz.getLayout({
        'appId' : appId,
        'scr': lScreen,
        'deviceGroup': this.currDeviceGroup
    });
    var lContent = null;
    if (this.apz.loDefsMap[appId][lScreen + this.apz.idSep + layout].customize && this.apz.loDefsMap[appId][lScreen + this.apz.idSep + layout].customize != "N") {
        var lPath = this.apz.getDataFilesPath(appId) + '/' + lScreen + 'cz.json';
        lContent = this.apz.getFile(lPath);
    }
    if (!this.apz.isNull(lContent)) {
        this.sDesignerData = JSON.parse(lContent);
        if (!this.sDesignerData.Microapps || !this.sDesignerData.Navigators || !this.sDesignerData.Widgets || !this.sDesignerData.Containers) {
            var param = {
                'message': 'JSON Exception: Invalid format'
            };
            this.apz.dispMsg(param);
        } else {
            $('#Customizer__ps_pls_MicroApps_div,#Customizer__ps_pls_Navigators_div,#Customizer__ps_pls_Widgets_div').removeClass('sno');
            if (this.sDesignerData.Microapps.length == 0) {
                $('#Customizer__ps_pls_MicroApps_div').addClass('sno');
            }
            if (this.sDesignerData.Navigators.length == 0) {
                $('#Customizer__ps_pls_Navigators_div').addClass('sno');
            }
            if (this.sDesignerData.Widgets.length == 0) {
                $('#Customizer__ps_pls_Widgets_div').addClass('sno');
            }
            this.sContainerData = this.sDesignerData.Containers[lTempid];
            $('#CMZR01__Customizer__editBtn').removeAttr('disabled');
            $('#CMZR01__Customizer__gr_row_designerData').removeClass('sno');
            $('#CMZR01__Customizer__MicroAppsList ul,#CMZR01__Customizer__NavigatorsList ul,#CMZR01__Customizer__CallFormsList ul').html('');
            $.each(this.sDesignerData, function(key, value) {
                for (var i = 0; i < value.length; i++) {
                    var lAppDetails = value[i];
                    if (key == 'Microapps') {
                        var lIcon = lAppDetails.microAppIcon;
                        var lName = lAppDetails.microAppName;
                        var lId = lAppDetails.microAppId;
                        var lClass = 'MicroAppsList';
                        var lParent = $('#CMZR01__Customizer__MicroAppsList ul');
                    } else if (key == 'Navigators') {
                        var lIcon = lAppDetails.navigatorIcon;
                        var lName = lAppDetails.navigatorName;
                        var lId = lAppDetails.navigatorId;
                        var lClass = 'NavigatorsList';
                        var lParent = $('#CMZR01__Customizer__NavigatorsList ul');
                    } else if (key == 'Widgets') {
                        var lIcon = lAppDetails.widgetIcon;
                        var lName = lAppDetails.widgetName;
                        var lId = lAppDetails.widgetId;
                        var lClass = 'CallFormsList';
                        var lParent = $('#CMZR01__Customizer__CallFormsList ul');
                    }
                    if (key !== 'Containers') {
                        var lHtml = '<li id=\'CMZR01__Customizer__' + lClass + '_row_' + i + '\' rowno=\'' + i + '\' class=\'pri srb wrapped ' +
                            lClass +
                            '\' ><span class=\'scb-sml100 scb-med100 scb-lar100 scb-xxl100 appArea wrapped pri\' id=\'CMZR01__Customizer__sc_col_12_li\'><span class=\'  pri wrapped\' id=\'CMZR01__Customizer__sc_row_7_row\'><span class=\'scb-sml100 scb-med100 scb-lar100 scb-xxl100 cen wrapped pri\' id=\'CMZR01__Customizer__sc_col_9_li\'><img class=\'ett-imge  tb52\' src=\'apps/styles/themes/' +
                            apz.theme + '/img/' + lIcon + '\' rowno=' + i + ' id=\'CMZR01__Designer__o__' + key + '__Icon_' + i +
                            '\'><span class=\'sno\' rowno=' + i + ' type=\'SVG\' id=\'CMZR01__Designer__o__' + key +
                            '__Icon_0_svg\'></span><p rowno=' + i + ' class=\'ett-para pri fs10\' id=\'CMZR01__Designer__o__' + key + '__Name_' +
                            i + '\'>' + lName + '</p><p rowno=' + i + ' class=\'ett-para pri fs10 sno appId\' id=\'CMZR01__Designer__o__' + key +
                            '__Id_' + i + '\'>' + lId + '</p></span></span></span></li>';
                        lParent.append(lHtml);
                    }
                }
            });
            this.fnEditorPostInit();
            if (this.isEditMode) {
                this.fnDesignerSetDraggable();
                this.fnEditorSetDroppable();
            }
        }
    } else {
        $('#CMZR01__Customizer__saveBtn,#CMZR01__Customizer__cancelBtn,#CMZR01__Customizer__gr_row_designerData').addClass('sno');
        $('#CMZR01__Customizer__editBtn').attr('disabled', 'disabled');
    }
};
Apz.Customizer.prototype.fnDesignerOnchangeDD = function(pelem, ptype) {
    var lDeviceGroup, lScreen;
    if (pelem == 'DG') {
        var allScreens = this.getAllScreens();
        var lScreenArr = [];
        for(var key in allScreens){
           for (var i = 0; i < allScreens[key].length; i++) {
            var lObj = {
                'val': key+"~"+allScreens[key][i].SCRDISPLAYNAME,
                'desc': allScreens[key][i].SCRDISPLAYNAME
            };
            lScreenArr.push(lObj);
          }   
        }
        lScreenArr.sort(function(a,b){
            if(a.desc.toUpperCase() > b.desc.toUpperCase()){
                return 1;
            }
            if(a.desc.toUpperCase() < b.desc.toUpperCase()){
                return -1;
            }
            return 0;
        });
        this.fnPopulateDropdown(document.getElementById('CMZR01__Customizer__screenDD'), lScreenArr);
        var myObj = this;
        var selectedScreen = allScreens[myObj.apz.currAppId].find(function(element){
           return element.SCREENID == myObj.apz.firstPage;
        }).SCRDISPLAYNAME;
        $('#CMZR01__Customizer__screenDD').val(selectedScreen);
        $('#CMZR01__Customizer__screenDD').parent().find("li").removeClass("is-selected");
        $('#CMZR01__Customizer__screenDD').parent().find("li").each(function() {
           if ($(this).attr("Value") == myObj.apz.currAppId+'~'+myObj.apz.firstPage) {
                $(this).addClass("is-selected");
            }
        });
    } else if (pelem == 'SCR') {
        $('#CMZR01__Customizer__Carousel').html('');
        var lCarouselHtml =
            ' <div class="swiper-container swipe-design" id="Customizer__Swiper"><div class="swiper-wrapper" ></div><div class="swiper-pagination" ></div><div class="swiper-button-next" ></div><div class="swiper-button-prev"  ></div></div>';
        $('#CMZR01__Customizer__Carousel').append(lCarouselHtml);
        var lClass = '';
        var selectedScreen = $('#CMZR01__Customizer__screenDD').parent().find('li.is-selected')[0].attributes.value.value;
        var selectedAppId = selectedScreen.split('~')[0];
        selectedScreen = this.allScreens[selectedAppId].find(function(element){
           return element.SCRDISPLAYNAME == selectedScreen.split('~')[1];
        }).SCREENID;
        if(this.apz.isNull(this.apz.isAppLaunched(selectedAppId))){
            var launchAppParams = {
                "appId" : selectedAppId
            };
            this.launchApp(launchAppParams);
        }
        var lParams = {
            'appId': selectedAppId,
            'scr': selectedScreen,
            'deviceGroup': this.currDeviceGroup
        };
        var lDesigns = this.apz.getDesigns(lParams);
        var myObj = this;
        var swiper = new Swiper('#Customizer__Swiper', {
            pagination: '.swiper-pagination',
            nextButton: '.swiper-button-next',
            prevButton: '.swiper-button-prev',
            slidesPerView: 1,
            centeredSlides: true,
            paginationClickable: true,
            spaceBetween: 30
        });
        if (!this.apz.isNull(this.currDesign) && (lDesigns.designs.indexOf(this.currDesign) > -1)) {
            var lDesign = this.currDesign;
        } else {
            var lDesign = lDesigns.currentDesign;
        }
        this.currDesign = lDesign;
        for (var i = 0; i < lDesigns.designs.length; i++) {
            if (lDesigns.designs[i] == lDesign) {
                lClass = 'currentTemplate';
            } else {
                lClass = '';
            }
            if (apz.isNull(lDesigns.icons[i])) {
                swiper.appendSlide('<div class="swiper-slide ' + lClass + '"><div id="CMZR01__Customizer__Carousel__' + lDesigns.designs[i] +
                    '" rowno="0" onclick="apz.customizer.fnTemplateClick(this)" style="width:130px; margin-left:70px;height:100px;" class="ett-imge  "> ' +
                    (lDesigns.designDisplayNames[i]) + ' </div></div>');
            } else {
                swiper.appendSlide('<div class="swiper-slide ' + lClass + '"><img id="CMZR01__Customizer__Carousel__' + lDesigns.designs[i] +
                    '" rowno="0" onclick="apz.customizer.fnTemplateClick(this)" style="height:100px;" src="apps/styles/themes/' + apz.theme + '/img/' +
                    lDesigns.icons[i] + '" class="ett-imge swiper-img "></div>');
            }
        }
        var lSlide = document.getElementsByClassName('swiper-slide');
        for (var i = 0; i < lSlide.length; i++) {
            if ($(lSlide[i]).hasClass('currentTemplate')) {
                var lSlideto = i;
            }
        }
        swiper.slideTo(lSlideto);
        $(".swiper-img").mouseenter(function() {
            apz.customizer.fnzoomImage('enter');
        });
        $(".swiper-img").mouseout(function() {
            apz.customizer.fnzoomImage('leave');
        });
        if(this.currScr !== selectedScreen){
            $("#CMZR01__Customizer__editBtn").attr("disabled","disabled");
        }else{
            $("#CMZR01__Customizer__editBtn").removeAttr("disabled");
        }
        //Call Appzillon api to get the list of Microapps, Callforms and Navigators mapped to the screen.
    } else if (pelem == 'DNR') {
        var lparam = {
            "action": "apply"
        };
        this.fnDesignerLaunch(lparam);
    } //Call Appzillon api to obtain layout and various designs maintained for the screen. Also the current applied design.
};
Apz.Customizer.prototype.fnPopulateDropdown = function(obj, options) {
    var childUl = document.createElement('ul');
    for (var i = 0; i < options.length; i++) {
        var opt = $('<li value=\'' + options[i].val + '\'>' + options[i].desc + '</li>');
        if (i == 0) {
            $(opt).addClass('is-selected');
            $(obj).val(options[i].desc);
        }
        childUl.appendChild(opt[0]);
    }
    var appender = $(obj).siblings('div');
    $(appender[0]).children().remove();
    $(appender[0]).append(childUl);
    this.apz.dropdownApz(obj);
};
Apz.Customizer.prototype.fnDesignerSetDraggable = function() {
    $('#CMZR01__Customizer__MicroAppsList li,#CMZR01__Customizer__CallFormsList li,#CMZR01__Customizer__NavigatorsList li').draggable({
        helper: 'clone',
        cursorAt: {
            top: 5,
            left: 5
        },
        drag: function(event, ui) {
            $(ui.helper[0]).css('width', '100px');
            apz.customizer.dragged = true;
            apz.customizer.draggedElement = $(this).parents('.parentContainer').attr('id');
            $('.appArea').css('cursor', 'move');
            if ($(this).hasClass('MicroAppsList')) {
                for (i = 0; i < apz.customizer.sContainerData.length; i++) {
                    if (apz.customizer.sContainerData[i].MA) {
                        $('#' + apz.customizer.sContainerData[i].ContainerId).css('height', '0px');
                        $('#' + apz.customizer.sContainerData[i].ContainerId).css('min-height', '200px');
                        document.getElementById(apz.customizer.sContainerData[i].ContainerId).scrollIntoView();
                    }
                }
            } else if ($(this).hasClass('CallFormsList')) {
                for (i = 0; i < apz.customizer.sContainerData.length; i++) {
                    if (apz.customizer.sContainerData[i].CF) {
                        var lCurrAppNo = $('#' + apz.customizer.sContainerData[i].ContainerId).children().not('.sno').length;
                        if ((lCurrAppNo % 2) == 0) {
                            lCurrAppNo = lCurrAppNo + 2;
                        } else {
                            lCurrAppNo++;
                        }
                        $('#' + apz.customizer.sContainerData[i].ContainerId).css('min-height', (340 * lCurrAppNo / 2) + 'px');
                    }
                }
            } else if ($(this).hasClass('NavigatorsList')) {
                for (i = 0; i < apz.customizer.sContainerData.length; i++) {
                    if (apz.customizer.sContainerData[i].NV) {
                        $('#' + apz.customizer.sContainerData[i].ContainerId).css('min-height', '200px');
                    }
                }
            }
        }
    });
    $('.appArea').mouseover(function() {
        apz.customizer.fnDesignerDragcheck(this);
    });
};
Apz.Customizer.prototype.fnTemplateClick = function(pthis) {
    if (this.isEditMode) {
        if (!$(pthis).hasClass('currentTemplate')) {
            $('.currentTemplate').removeClass('currentTemplate');
            $('#Customizer__Swiper .swiper-wrapper .swiper-slide-active').addClass('currentTemplate');
        }
    }
};
Apz.Customizer.prototype.fnEditorSetDroppable = function() {
    mouseEntered = false;
    for (i = 0; i < this.sContainerData.length; i++) {
        $('#' + this.sContainerData[i].ContainerId).css('border', '1px dotted');
        $('#' + this.sContainerData[i].ContainerId).css('min-height', '350px');
    }
    for (i = 0; i < this.sContainerData.length; i++) {
        var lContainerId = this.sContainerData[i].ContainerId;
        $('#' + lContainerId).mouseenter(function() {
            lContainerId = this.id;
            for (j = 0; j < apz.customizer.sContainerData.length; j++) {
                if (apz.customizer.sContainerData[j].ContainerId == lContainerId) {
                    lIsMAAllowed = apz.customizer.sContainerData[j].MA;
                    lIsCFAllowed = apz.customizer.sContainerData[j].CF;
                }
            }
            if (this.dragged) {
                if (lIsMAAllowed) {
                    if (apz.customizer.draggedElement == 'CMZR01__Customizer__MicroAppsList' || apz.customizer.draggedElement ==
                        'CMZR01__Customizer__NavigatorsList') {
                        if ($('#dropMicroApp').length == 0) {
                            $('#' + lContainerId).prepend('<p id="dropMicroApp" class="tcenter"> Drop MicroApps Here</p>');
                        }
                        $('#' + lContainerId).css('cursor', 'crosshair');
                    } else {
                        $('#' + lContainerId).css('cursor', 'not-allowed');
                    }
                } else if (lIsCFAllowed) {
                    if (this.draggedElement == 'CMZR01__Customizer__CallFormsList') {
                        if ($('#dropCallForm').length == 0) {
                            $('#' + lContainerId).prepend('<p id="dropCallForm" class="tcenter"> Drop Widgets Here</p>');
                        }
                        $('#' + lContainerId).css('cursor', 'crosshair');
                    } else {
                        $('#' + lContainerId).css('cursor', 'not-allowed');
                    }
                }
            } else {
                $('#' + lContainerId).css('cursor', 'default');
            }
        });
    }
    for (i = 0; i < this.sContainerData.length; i++) {
        var lContainerId = this.sContainerData[i].ContainerId;
        $('#' + lContainerId).mouseleave(function() {
            $('#dropMicroApp').remove();
            $(this).css('min-height', 'auto')
        });
    }
    for (i = 0; i < this.sContainerData.length; i++) {
        var lContainerId = this.sContainerData[i].ContainerId;
        $('#' + lContainerId).mouseup(function() {
            $('#dropMicroApp').remove();
            $(this).css('min-height', 'auto')
        });
        $('#' + lContainerId).droppable({
            tolerance: 'touch',
            drop: function(event, ui) {
                apz.customizer.isModified = true;
                apz.customizer.dragged = false;
                $('#' + lContainerId).css('cursor', 'default');
                if ($(ui.draggable).hasClass('MicroAppsList') && lIsMAAllowed) {
                    apz.customizer.fnEditorMicroAppDrop(lContainerId, ui.draggable.find('.appId').text());
                    $('#' + lContainerId).css('min-height', 'auto');
                    $('#dropMicroApp').remove();
                } else if ($(ui.draggable).hasClass('NavigatorsList')) {
                    apz.customizer.fnEditorNavigatorDrop(lContainerId, ui.draggable.find('.appId').text());
                    $('#' + lContainerId + ' > div:last-child').remove();
                } else if ($(ui.draggable).hasClass('CallFormsList') && lIsCFAllowed) {
                    apz.customizer.fnEditorCallFormDrop(lContainerId, ui.draggable.find('.appId').text());
                    $('#' + lContainerId).css('min-height', 'auto');
                    $('#dropCallForm').remove();
                }
            }
        });
    }
};
Apz.Customizer.prototype.fnEditorMicroAppDrop = function(pContainer, pDragTxt) {
    var lScreen = this.getCurrentScreen();
    var lApp = $('#' + pContainer).find('#' + this.apz.currAppId + '__' + lScreen + '__' + pDragTxt);
    lApp.removeClass('sno');
    $('#' + this.apz.currAppId + '__' + lScreen + '__MicroAppPanel').append(lApp);
};
Apz.Customizer.prototype.fnEditorNavigatorDrop = function(pContainer, pDragTxt) {
    var lScreen = this.getCurrentScreen();
    var lApp = $('#' + pContainer).find('#' + this.apz.currAppId + '__' + lScreen + '__' + pDragTxt);
    lApp.removeClass('sno');
    $('#' + pContainer).append(lApp);
};
Apz.Customizer.prototype.fnEditorCallFormDrop = function(pContainer, pDragTxt) {
    var lScreen = this.getCurrentScreen();
    var lApp = $('#' + pContainer).find('#' + this.apz.currAppId + '__' + lScreen + '__' + pDragTxt);
    lApp.removeClass('sno');
    $('#' + pContainer + ' .AppRow').parent().append(lApp);
};
Apz.Customizer.prototype.fnEditorShowNavigatorApps = function(pobj, event) {
    if ($(pobj).find('.NavigatorAppsContainer').hasClass('sno')) {
        $('.NavigatorAppsContainer').addClass('sno');
        $(pobj).find('.NavigatorAppsContainer').removeClass('sno');
    } else {
        $(pobj).find('.NavigatorAppsContainer').addClass('sno');
    }
    event.stopImmediatePropagation();
};
Apz.Customizer.prototype.fnDesignerCloseApp = function(pobj) {
    for (i = 0; i < this.sContainerData.length; i++) {
        $('#' + this.sContainerData[i].ContainerId).css('min-height', 'auto');
    }
    var lId = pobj.parentNode.id.split('_');
    lId = lId[lId.length - 1];
    if ($('#CMZR01__Customizer__gr_row_designerData p:contains("' + lId + '")').length == 0) {
        /* the particular widget/microapp ID isnt mentioned in the screen */
        var param = {
            'message': "ID in the designer JSON does not match the container ID on the screen"
        };
        this.apz.dispMsg(param);
    } else {
        this.isModified = true;
        $(pobj).parents('.AppRow').addClass('sno');
    }
};
Apz.Customizer.prototype.fnDesignerEditClick = function() {
    this.isEditMode = true;
    $('#CMZR01__Customizer__screenDD_span').css('pointer-events', 'none');
    $('#CMZR01__Customizer__goBtn').attr('disabled', 'disabled');
    $('#CMZR01__Customizer__applyBtn').removeClass('sno');
    $('.swiper-slide').css('pointer-events', 'unset');
    $('#CMZR01__Customizer__saveBtn,#CMZR01__Customizer__cancelBtn').removeClass('sno');
    $('#CMZR01__Customizer__editBtn').addClass('sno');
    this.fnDesignerSetDraggable();
    this.fnEditorSetDroppable();
    setTimeout(function() {
        var lCloseIcon =
            '<button enabled="enabled" class="ett-bttn tsp min icl icon-close" onclick="apz.customizer.fnDesignerCloseApp(this)"  style="position:absolute" type="button"><svg class="ett-icon icon-remove px20 " aria-hidden="true" id="btn_icon_icon-remove"><use xlink:href="#icon-remove"/></svg><span></span></button>';
        $('.AppRow').append(lCloseIcon);
    }, 50);
};
Apz.Customizer.prototype.fnSaveBtnClick = function() {
    this.isEditMode = false;
    this.save();
};
Apz.Customizer.prototype.save = function() {
    var req = {
        'appzillonSaveCustomizationDataRequest': {
            'CurrentDesign': {
                'appId': this.apz.currAppId,
                'screenId': this.currScr,
                'layoutId': this.apz.getLayout(this.currScr),
                'designId': apz.customizer.currDesign
            },
            'DesignReceivers': []
        }
    };
    for (var i = 0; i < this.sContainerData.length; i++) {
        var lAppSeq = 0;
        var lContainer = this.sContainerData[i];
        $('#' + lContainer.ContainerId + ' .AppRow').each(function() {
            if (!$(this).hasClass('sno')) {
                var lArr = {
                    'appId': apz.currAppId,
                    'screenId': apz.customizer.currScr,
                    'layoutId': apz.getLayout(apz.currScr),
                    'designId': apz.customizer.currDesign,
                    'parentId': $(this).parent().attr('id'),
                    'childId': $(this).attr('id'),
                    'childSeq': lAppSeq++
                };
                req.appzillonSaveCustomizationDataRequest.DesignReceivers.push(lArr);
            }
        });
    }
    var lServerParams = {
        'ifaceName': 'appzillonSaveCustomizationData',
        'callBackObj': this,
        'buildReq': 'N',
        'req': req,
        'paintResp': 'N',
        'internal': true,
        'callBack': this.appzillonSaveCustomizationDataCB
    };
    this.appIdBackUp = this.apz.appId;
    this.apz.appId = 'Admin';
    this.apz.userId = this.userId;
    this.apz.mockServer = false;
    this.apz.server.sendReq(lServerParams);
};
Apz.Customizer.prototype.appzillonSaveCustomizationDataCB = function(params) {
    this.apz.mockServer = true;
    this.apz.appId = this.appIdBackUp;
    if (params.errors) {
        var param = {
            'code': params.errors[0].errorCode
        };
        this.apz.dispMsg(param);
    } else {
        if (!this.apz.isNull(params.res)) {
            this.isModified = false;
            var lCurrDesign = params.req.appzillonSaveCustomizationDataRequest.CurrentDesign;
            var lKey = lCurrDesign.screenId + '__' + lCurrDesign.layoutId + '__' + lCurrDesign.designId;
            if (!this.config[lCurrDesign.appId][lKey]) {
                this.config[lCurrDesign.appId][lKey] = {};
            }
            this.config[lCurrDesign.appId][lKey] = params.res.appzillonSaveCustomizationDataResponse;
            $('#CMZR01__Customizer__screenDD_span').css('pointer-events', 'auto');
            $('#CMZR01__Customizer__saveBtn,#CMZR01__Customizer__cancelBtn').addClass('sno');
            $('#CMZR01__Customizer__editBtn').removeClass('sno');
            $('#CMZR01__Customizer__applyBtn').addClass('sno');
            $('#CMZR01__Customizer__goBtn').removeAttr('disabled');
            $('.icon-remove').remove();
            for (i = 0; i < this.sContainerData.length; i++) {
                $('#' + this.sContainerData[i].ContainerId).css('border', 'none');
            }
            for (i = 0; i < this.sContainerData.length; i++) {
                $('#' + this.sContainerData[i].ContainerId).css('min-height', 'auto');
            }
            $('.appArea').unbind('mouseover').css('cursor', 'default');
            $('.appArea').parent().draggable('disable');
        }
    }
};
Apz.Customizer.prototype.cancel = function() {
    this.isEditMode = false;
    this.isModified = false;
    $('#CMZR01__Customizer__screenDD_span').css('pointer-events', 'auto');
    $('#CMZR01__Customizer__applyBtn').addClass('sno');
    $('#CMZR01__Customizer__goBtn').removeAttr('disabled');
    $('#CMZR01__Customizer__saveBtn,#CMZR01__Customizer__cancelBtn').addClass('sno');
    $('#CMZR01__Customizer__editBtn').removeClass('sno');
    var lparam = {
        "action": "cancel"
    };
    this.fnDesignerLaunch(lparam);
};
Apz.Customizer.prototype.fnDesignerApplyClick = function() {
    var lTempid = $('.currentTemplate').children()[0].id.split('_');
    lTempid = lTempid[lTempid.length - 1];
    if (this.currDesign !== lTempid) {
        if (this.isModified) {
            var params = {
                'message': 'Modifications are done! would you like to save the changes?',
                'type': 'C',
                'callBack': this.fnModifiedCB,
                'callBackObj': this
            };
            apz.dispMsg(params);
        } else {
            this.fnLaunchDesign()
        }
    }
};
Apz.Customizer.prototype.fnLaunchDesign = function() {
    var lTempid = $('.currentTemplate').children()[0].id.split('_');
    this.currDesign = lTempid[lTempid.length - 1];
    apz.customizer.fnDesignerOnchangeDD('DNR');
    this.fnDesignerSetDraggable();
    this.fnEditorSetDroppable();
    setTimeout(function() {
        var lCloseIcon =
            '<button enabled="enabled" class="ett-bttn tsp min icl icon-close" onclick="apz.customizer.fnDesignerCloseApp(this)"  style="position:absolute" type="button"><svg class="ett-icon icon-remove px20 " aria-hidden="true" id="btn_icon_icon-remove"><use xlink:href="#icon-remove"/></svg><span></span></button>';
        $('.AppRow').append(lCloseIcon);
    }, 50);
};
Apz.Customizer.prototype.fnModifiedCB = function(params) {
    if (params.choice) {
        this.save();
        this.fnLaunchDesign();
        $('#CMZR01__Customizer__screenDD_span').css('pointer-events', 'none');
        $('#CMZR01__Customizer__saveBtn,#CMZR01__Customizer__cancelBtn').removeClass('sno');
        $('#CMZR01__Customizer__editBtn').addClass('sno');
        $('#CMZR01__Customizer__applyBtn').removeClass('sno');
        $('#CMZR01__Customizer__goBtn').attr('disabled', 'disabled');
    }
};
Apz.Customizer.prototype.refreshDesigner = function(params) {
    var lKey = params.scr + '__' + params.lo + '__' + params.template;
    if (apz.scrDefsMap[params.appId][params.scr + '__' + params.lo + '__' + params.template].scrType == 'MAIN') {
        this.prevScr = this.currScr;
        this.currScr = params.scr;
        var selectedScreen = this.allScreens[params.appId].find(function(element){
           return element.SCREENID == params.scr;
        }).SCRDISPLAYNAME;
        this.apz.setElmValue('CMZR01__Customizer__screenDD', selectedScreen);
        $('#CMZR01__Customizer__screenDD').parent().find("li").removeClass("is-selected");
        $('#CMZR01__Customizer__screenDD').parent().find("li").each(function() {
          if ($(this).attr("Value") == params.appId+'~'+selectedScreen) {
                $(this).addClass("is-selected");
            }
        });
        if (this.prevScr !== this.currScr || params.appId !== this.apz.currAppId) { 
            this.fnDesignerOnchangeDD('SCR');
        }
    }
    this.fnDesignerRender();
};
Apz.Customizer.prototype.loadDeviceGroups = function() {
    this.apz.startLoader();
    var deviceGroups = this.getDeviceGroup();
    var deviceGroupsDDArray = [];
    for (var k = 0; k < deviceGroups.length; k++) {
        deviceGroupsDDArray[k] = {
            'val': deviceGroups[k].name,
            'desc': deviceGroups[k].name
        }
    }
    this.fnPopulateDropdown(document.getElementById('DeviceGroup'), deviceGroupsDDArray);
    this.deviceGroupChanged();
    this.apz.stopLoader();
};
Apz.Customizer.prototype.deviceGroupChanged = function() {
    var selectedValue = $('#DeviceGroup').val().trim();
    var selectedGroup;
    for (var k = 0; k < this.deviceGroups.length; k++) {
        if (this.deviceGroups[k].name == selectedValue) {
            selectedGroup = this.deviceGroups[k];
            this.currDeviceGroup = selectedValue;
            break;
        }
    }
    if (!this.apz.isNull(selectedGroup)) {
        var windowWidthToSet = parseInt(selectedGroup.width, 10) + 300;
        var windowHeightToSet = parseInt(selectedGroup.height, 10);
        var screenWidth = screen.width;
        windowWidthToSet = windowWidthToSet > screenWidth ? screenWidth : windowWidthToSet;
        window.resizeTo(windowWidthToSet, windowHeightToSet);
        elementQuery();
    }
};
Apz.Customizer.prototype.getDeviceGroup = function() {
    this.apz.currAppId = this.apz.appId;
    var path = this.apz.getConfigPath() + '/' + 'devicegroups.json';
    var params = {};
    params.path = path;
    params.async = false;
    params.id = 'DEVICEGROUP';
    params.callBack = null;
    params.content = this.apz.getFile(params);
    var deviceGroups = JSON.parse(params.content);
    this.deviceGroups = deviceGroups.deviceGroups;
    return this.deviceGroups;
};
Apz.Customizer.prototype.getAllScreens = function(){
    this.apz.currAppId = this.apz.appId;
    var path = this.apz.getConfigPath() + '/' + 'prjdef.json';
    var params = {};
    params.path = path;
    params.async = false;
    params.id = 'SCREENDETAILS';
    params.callBack = null;
    params.content = this.apz.getFile(params);
    var screenDetails = JSON.parse(params.content);
    this.allScreens = screenDetails.screenDetails;
    return this.allScreens;
};
Apz.Customizer.prototype.login = function() {
    var userId = $('#userId').val().trim();
    var password = $('#pswd').val().trim();
    if (apz.isNull(userId) || apz.isNull(password)) {
        var params = {
            'code': 'APZ-LOG-ERR'
        };
        apz.dispMsg(params);
    } else {
        this.appIdBackUp = this.apz.appId;
        this.apz.appId = 'Admin';
        var req = {};
        req.userId = userId;
        req.pwd = password;
        req.callBackObj = this;
        req.callBack = this.loginCB;
        apz.server.login(req);
    }
};
Apz.Customizer.prototype.loginCB = function(parameters) {
    if (parameters.status && parameters.res.loginResponse.status && this.apz.isNull(parameters.errors)) {
        this.apz.appId = this.appIdBackUp;
        this.currDeviceGroup = $('#DeviceGroup').val().trim();
        $('#Login__gr_row_1').addClass('sno');
        $('#page_1,#CMZR01__Customizer__gr_row_1').removeClass('sno');
        this.apz.loadProject();
        this.apz.mockServer = true;
        this.userId = parameters.res.loginResponse.userDet.id;
        this.apz.deviceGroup = this.currDeviceGroup;
    } else {
        var params = {
            'message': 'Login Failed.'
        };
        apz.dispMsg(params);
    }
};
Apz.Customizer.prototype.logout = function() {
    var request = {};
    request.userId = this.userId;
    request.callBackObj = this;
    request.callBack = this.logoutCB;
    this.apz.userId = this.userId;
    this.apz.appId = 'Admin';
    this.apz.mockServer = false;
    apz.server.logout(request);
};
Apz.Customizer.prototype.logoutCB = function(params) {
    if (params.status) {
        if (this.apz.isNull(params.errors)) {
            window.close();
        } else {
            if (params.errors[0].errorCode[0] !== '$') {
                var msg = {
                    'code': params.errors[0].errorCode
                }
                this.apz.dispMsg(msg);
            }
        }
    } else {
        msg = {
            'code': 'APZ-SVR-ERR'
        };
        this.apz.dispMsg(msg);
    }
};
Apz.Customizer.prototype.fnzoomImage = function(pEvent) {
        if (pEvent == 'enter') {
            var url = $('#Customizer__Swiper .swiper-slide-active img').attr('src');
                var widthToShow = $('.deviceDesignDiv').width();
                var lZoomDiv = document.getElementById('zoom_templateIconZoom');
                $('.deviceDesignDiv').not('.sno').css('position', 'relative');
                $('.deviceDesignDiv').not('.sno').append('<div class="tempBg "></div>');
                $('.deviceDesignDiv').not('.sno').append(lZoomDiv);
                $('#zoom__templateIcon').attr('src', url);
                $('#zoom_templateIconZoom').removeClass('sno');
        } else if (pEvent == 'leave') {
            $('#zoom_templateIconZoom').addClass('sno')
            $('.tempBg').remove();
            var lZoomDiv = document.getElementById('zoom_templateIconZoom');
            $('#pt-main').not('.sno').append(lZoomDiv);
        }
};
Apz.Customizer.prototype.getCurrentScreen = function(){
    var selectedScreen = $('#CMZR01__Customizer__screenDD').parent().find('li.is-selected')[0].attributes.value.value;
    var selectedAppId = selectedScreen.split('~')[0];
        selectedScreen = this.allScreens[selectedAppId].find(function(element){
           return element.SCRDISPLAYNAME == selectedScreen.split('~')[1];
        }).SCREENID;
    return selectedScreen;
};
Apz.Customizer.prototype.getselectedApp = function(){
    var selectedScreen = $('#CMZR01__Customizer__screenDD').parent().find('li.is-selected')[0].attributes.value.value;
    return selectedScreen.split('~')[0];;
};
Apz.Customizer.prototype.launchApp = function(params) {
         params.oldAppId = this.apz.currAppId;
         this.apz.currAppId = params.appId;
         params.callBack = this.microAppDefLoaded;
         params.callBackObj = this;
         params.runnerObj = this;
         params.path = this.apz.getConfigPath(params.appId) + "/" + "prjdef.json";
         this.apz.getFile(params);
}; 
Apz.Customizer.prototype.microAppDefLoaded = function(params){
      if(params.content){
         var def = JSON.parse(params.content);
         this.apz.scrDefsMap[params.appId] = {};
         this.apz.loDefsMap[params.appId] = {};
         if(this.apz.customizer){
            this.config[params.appId] = {};
            this.configMeta[params.appId] = {};
         }
         if(!this.apz[params.appId]){
            this.apz[params.appId] = {};
         }
         this.apz.scrHtmls[params.appId] = {};
         this.apz.ifacesMap[params.appId] = {};
         this.apz.loMap[params.appId] = def.loMap;
         this.apz.msgs[params.appId] = def.msgs[this.language];
         this.apz.lovs[params.appId] = def.lovs;
         this.apz.scrs[params.appId] = def.screens;
         this.apz.appsMap[params.appId] = {"scripts":def.scripts};
      } else {
         this.apz.currAppId = params.oldAppId;
         var param = {"code":"APZ-CNT-226"}
         this.apz.dispMsg(param);
      }
};
/******* Element Query Polyfill **********/
/******* Added by shyam to support media queries for different sized windows ******/
/*! elementQuery | Author: Tyson Matanich (http://matanich.com), 2013 | License: MIT */
var elementQuery = (function(window, document, undefined) {
    // Enable strict mode
    'use strict';
    // Use Sizzle standalone or from jQuery
    var sizzle = jQuery.find;
    // Set the number of sizzle selectors to cache (default is 50)
    //sizzle.selectors.cacheLength = 50;
    var queryData = {};
    var cssRules = null;
    var setCssRules = function() {
        if (document.styleSheets[0]) {
            cssRules = (document.styleSheets[0].cssRules !== undefined) ? 'cssRules' : 'rules';
        }
    }
    var addQueryDataValue = function(selector, type, pair, number, value) {
        selector = trim(selector);
        if (selector != '') {
            var parts;
            if (!number && !value) {
                parts = /^([0-9]*.?[0-9]+)(px|em)$/.exec(pair)
                if (parts != null) {
                    number = Number(parts[1]);
                    if (number + '' != 'NaN') {
                        value = parts[2];
                    }
                }
            }
            if (value) {
                // Compile the sizzle selector
                if (sizzle.compile) {
                    sizzle.compile(selector);
                } // Update the queryData object
                if (queryData[selector] === undefined) {
                    queryData[selector] = {};
                }
                if (queryData[selector][type] === undefined) {
                    queryData[selector][type] = {};
                }
                queryData[selector][type][pair] = [
                    number,
                    value
                ];
            }
        }
    };
    var updateQueryData = function(data, doUpdate) {
        var i,
            j,
            k;
        for (i in data) {
            for (j in data[i]) {
                if (typeof data[i][j] == 'string') {
                    addQueryDataValue(i, j, data[i][j]);
                } else if (typeof data[i][j] == 'object') {
                    for (k = 0; k < data[i][j].length; k++) {
                        addQueryDataValue(i, j, data[i][j][k]);
                    }
                }
            }
        }
        if (doUpdate == true) {
            refresh();
        }
    };
    var processSelector = function(selectorText) {
        if (selectorText) {
            var regex =
                /(\[(min\-width|max\-width|min\-height|max\-height)\~\=(\'|\")([0-9]*.?[0-9]+)(px|em)(\'|\")\])(\[(min\-width|max\-width|min\-height|max\-height)\~\=(\'|\")([0-9]*.?[0-9]+)(px|em)(\'|\")\])?/gi;
            // Split out the full selectors separated by a comma ','
            var selectors = selectorText.split(',');
            var i,
                selector,
                result,
                number,
                prevIndex,
                k,
                tail,
                t;
            for (i = 0; i < selectors.length; i++) {
                selector = null;
                prevIndex = 0;
                k = 0;
                while (k == 0 || result != null) {
                    result = regex.exec(selectors[i]);
                    if (result != null) {
                        // result[2] = min-width|max-width|min-height|max-height
                        // result[4] = number
                        // result[5] = px|em
                        // result[7] = has another
                        // Ensure that it contains a valid numeric value to compare against
                        number = Number(result[4]);
                        if (number + '' != 'NaN') {
                            if (selector == null) {
                                // New set: update the current selector
                                selector = selectors[i].substring(prevIndex, result.index);
                                // Append second half of the selector
                                tail = selectors[i].substring(result.index + result[1].length);
                                if (tail.length > 0) {
                                    t = tail.indexOf(' ');
                                    if (t != 0) {
                                        if (t > 0) {
                                            // Take only the current part
                                            tail = tail.substring(0, t);
                                        } // Remove any sibling element queries
                                        tail = tail.replace(
                                            /(\[(min\-width|max\-width|min\-height|max\-height)\~\=(\'|\")([0-9]*.?[0-9]+)(px|em)(\'|\")\])/gi,
                                            '');
                                        selector += tail;
                                    }
                                }
                            } // Update the queryData object
                            addQueryDataValue(selector, result[2], result[4] + result[5], number, result[5]);
                        }
                        if (result[7] === undefined || result[7] == '') {
                            // Reached the end of the set
                            prevIndex = result.index + result[1].length;
                            selector = null;
                        } else {
                            // Update result index to process next item in the set
                            regex.lastIndex = result.index + result[1].length;
                        }
                    }
                    k++;
                }
            }
        }
    };
    var processStyleSheet = function(styleSheet, force) {
        if (cssRules == null) {
            setCssRules();
        }
        if (styleSheet[cssRules] && styleSheet[cssRules].length > 0) {
            var ownerNode = styleSheet.ownerNode || styleSheet.owningElement;
            if (force || (ownerNode.getAttribute('data-elementquery-bypass') === null && ownerNode.getAttribute('data-elementquery-processed') ===
                null)) {
                var i,
                    j,
                    rule;
                for (i = 0; i < styleSheet[cssRules].length; i++) {
                    rule = styleSheet[cssRules][i];
                    // Check nested rules in media queries etc
                    if (rule[cssRules] && rule[cssRules].length > 0) {
                        for (j = 0; j < rule[cssRules].length; j++) {
                            processSelector(rule[cssRules][j].selectorText);
                        }
                    } else {
                        processSelector(rule.selectorText);
                    }
                } // Flag the style sheet as processed
                ownerNode.setAttribute('data-elementquery-processed', '');
            }
        }
    };
    // Refactor from jQuery.trim()
    var trim = function(text) {
        if (text == null) {
            return '';
        } else {
            var core_trim = ''.trim;
            if (core_trim && !core_trim.call('﻿ ')) {
                return core_trim.call(text);
            } else {
                return (text + '').replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, '');
            }
        }
    };
    // Refactor from jquery().addClass() and jquery().removeClass()
    var clean = function(element, attr) {
        // This expression is here for better compressibility
        var val = element.getAttribute(attr);
        return val ? (' ' + val + ' ').replace(/[\t\r\n]/g, ' ') : ' ';
    };
    // Refactor from jquery().addClass()
    var addTo = function(element, attr, value) {
        if (element.nodeType === 1) {
            var val = trim(value);
            if (val != '') {
                var cur = clean(element, attr);
                if (cur.indexOf(' ' + val + ' ') < 0) {
                    // Add the value if its not already there
                    element.setAttribute(attr, trim(cur + val));
                }
            }
        }
    };
    // Refactor from jquery().removeClass()
    var removeFrom = function(element, attr, value) {
        if (element.nodeType === 1) {
            var val = trim(value);
            if (val != '') {
                var cur = clean(element, attr);
                var updated = false;
                while (cur.indexOf(' ' + val + ' ') >= 0) {
                    // Remove the value
                    cur = cur.replace(' ' + val + ' ', ' ');
                    updated = true;
                }
                if (updated) {
                    // Update the attribute
                    element.setAttribute(attr, trim(cur));
                }
            }
        }
    };
    var init = function() {
        // Process the style sheets
        var i;
        for (i = 0; i < document.styleSheets.length; i++) {
            processStyleSheet(document.styleSheets[i]);
        }
        refresh();
    }
    var refresh = function() {
        var i,
            ei,
            j,
            k,
            elements,
            element,
            val;
        // For each selector
        for (i in queryData) {
            // Get the items matching the selector
            elements = sizzle(i);
            if (elements.length > 0) {
                // For each matching element
                for (ei = 0; ei < elements.length; ei++) {
                    element = elements[ei];
                    // For each min|max-width|height string
                    for (j in queryData[i]) {
                        // For each number px|em value pair
                        for (k in queryData[i][j]) {
                            val = queryData[i][j][k][0];
                            if (queryData[i][j][k][1] == 'em') {
                                // Convert EMs to pixels
                                val = val * (window.getEmPixels ? getEmPixels(element) : 16); // NOTE: Using getEmPixels() has a small performance impact
                            }
                            /* NOTE: Using offsetWidth/Height so an element can be adjusted when it reaches a specific size.
                            /* For Nested queries scrollWidth/Height or clientWidth/Height may sometime be desired but are not supported. */
                            if ((j == 'min-width' && element.offsetWidth >= val) || (j == 'max-width' && element.offsetWidth <= val) || (j ==
                                'min-height' && element.offsetHeight >= val) || (j == 'max-height' && element.offsetHeight <= val)) {
                                // Add matching attr value
                                addTo(element, j, k);
                            } else {
                                // Remove non-matching attr value
                                removeFrom(element, j, k);
                            }
                        }
                    }
                }
            }
        }
        if (!window.addEventListener && window.attachEvent) {
            // Force a repaint in IE7 and IE8
            var className = document.documentElement.className;
            document.documentElement.className = ' ' + className;
            document.documentElement.className = className;
        }
    } // Expose some public functions
    window.elementQuery = function(arg1, arg2) {
        if (arg1 && typeof arg1 == 'object') {
            if (arg1.cssRules || arg1.rules) {
                // Process a new style sheet
                processStyleSheet(arg1, true);
                if (arg2 == true) {
                    refresh();
                }
            } else {
                // Add new selector queries
                updateQueryData(arg1, arg2);
            }
        } else if (!arg1 && !arg2) {
            refresh();
        }
    };
    //NOTE: For development purposes only!
    window.elementQuery.selectors = function() {
        var data = {};
        var i,
            j,
            k;
        // For each selector
        for (i in queryData) {
            // For each min|max-width|height string
            for (j in queryData[i]) {
                // For each number px|em value pair
                for (k in queryData[i][j]) {
                    if (data[i] === undefined) {
                        data[i] = {};
                    }
                    if (data[i][j] === undefined) {
                        data[i][j] = [];
                    }
                    data[i][j][data[i][j].length] = k;
                }
            }
        }
        return data;
    };
    if (window.addEventListener) {
        window.addEventListener('resize', refresh, false);
        window.addEventListener('DOMContentLoaded', init, false);
        window.addEventListener('load', init, false);
    } else if (window.attachEvent) {
        window.attachEvent('onresize', refresh);
        window.attachEvent('onload', init);
    }
    return init;
}(this, document, undefined));
/*! getEmPixels  | Author: Tyson Matanich (http://matanich.com), 2013 | License: MIT */
(function(document, documentElement) {
    // Enable strict mode
    'use strict';
    // Form the style on the fly to result in smaller minified file
    var important = '!important;';
    var style = 'position:absolute' + important + 'visibility:hidden' + important + 'width:1em' + important + 'font-size:1em' + important +
        'padding:0' + important;
    window.getEmPixels = function(element) {
        var extraBody;
        if (!element) {
            // Emulate the documentElement to get rem value (documentElement does not work in IE6-7)
            element = extraBody = document.createElement('body');
            extraBody.style.cssText = 'font-size:1em' + important;
            documentElement.insertBefore(extraBody, document.body);
        } // Create and style a test element
        var testElement = document.createElement('i');
        testElement.style.cssText = style;
        element.appendChild(testElement);
        // Get the client width of the test element
        var value = testElement.clientWidth;
        if (extraBody) {
            // Remove the extra body element
            documentElement.removeChild(extraBody);
        } else {
            // Remove the test element
            element.removeChild(testElement);
        } // Return the em value in pixels
        return value;
    };
}(document, document.documentElement));