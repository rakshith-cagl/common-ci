import { NgModule } from '@angular/core';
import {ScreenComponentModule} from './screenComponent/screencomponent.Module';
import {BodyComponentModule} from './bodycomponent/bodycomponent.module';
import {GridRowComponentModule} from './gridrowcomponent/gridrowcomponent.module';
import {GridColComponentModule} from './gridcolcomponent/gridcolcomponent.module';
import {PanelComponentModule} from './panelcomponent/panelcomponent.module';
import {PanelSecComponentModule} from './panelseccomponent/panelseccomponent.module';
import {FormComponentModule} from './formcomponent/formcomponent.module';
import {SecRowComponentModule} from './secrowcomponent/secrowcomponent.module';
import {SecColComponentModule} from './seccolcomponent/seccolcomponent.module';
import {InputComponentModule} from './inputcomponent/inputcomponent.module';
import {ButtonComponentModule} from './buttoncomponent/buttoncomponent.module';
import {TableComponentModule} from './tablecomponent/tablecomponent.module';
import { TextAreaModule } from './text-area/text-area.component.module';
import {ListComponentModule} from './listcomponent/listcomponent.module';
import {BadgeComponentModule} from './badgecomponent/badge.component.module';
import {BulletComponentModule} from './bulletcomponent/bullet.component.module';
import {CardNumberModule} from './cardnumbercomponent/card-number-component.module';
import {CheckBoxComponentModule} from './check-boxcomponent/check-box.component.module';
import {CheckBoxGroupModule} from './checkboxgroupcomponent/checkbox-group-component.module';
import {DialogModule} from './dialogcomponent/dialog-component.module';
import {DropDownModule} from './drop-down/drop-down.module';
import { DropDownButtonModule} from './dropdownbuttoncomponent/dropdownbutton-component.module';
import {ExternalLinkComponentModule} from './external-linkcomponent/external-link.component.module';
import {FileBrowserModule} from './filebrowsercomponent/file-browser-component.module';
import {FooterModule} from './footercomponent/footer-component.module';
import {HeaderModule} from './headercomponent/header-component.module';
import {HyperLinkComponentModule} from './hyperlinkcomponent/hyper-link.component.module';
import {IconComponentModule} from './iconcomponent/icon.component.module';
import {ImageComponentModule} from './imagecomponent/image.component.module';
import {InputWithButtonComponentModule} from './inputwithbuttoncomponent/input-with-button.component.module';
import {LabelComponentModule} from './labelcomponent/label.component.module';
import {NavbarModule} from './navbarcomponeent/navbar-component.module';
import {PopOverModule} from './popovercomponent/pop-over-component.module';
import {ProgressBarComponentModule} from './progressbarcomponent/progress-bar.component.module';
import {ProgressStepComponentModule} from './progressstepcomponent/progress-step.component.module';
import {RadioButtonComponentModule} from './radiobuttoncomponent/radio-button.component.module';
import {SeparatorComponentModule} from './separatorcomponent/separator.component.module';
import {SideBarModule} from './sidebarcomponent/sidebar-component.module';
import {SliderComponentModule} from './slidercomponent/slider.component.module';
import {SortCodeModule} from './sortcodecomponent/sort-code-component.module';
import {StepperModule} from './steppercomponent/steppercomponent.module';
import {TagsInputModule} from './tagsInputComponent/tagsInputComponent.module';
import {TextModule} from './text/text.module';
import {ToggleModule} from './togglecomponent/togglecomponent.module';
import {ModalModule} from './modalcomponent/modal-component.module';
import { BreadCrumbModule } from './breadcrumbcomponent/breadcrumb-component.module';
import { BreadCrumbElementModule } from './breadcrumbelementcomponent/breadcrumbelement-component.module';
import { SortCodeAccountModule } from './sortcodeaccountcomponent/sortcode-account-component.module';
import { MenuModule } from './menucomponent/menu-component.module';
import { ChartModule } from './chartcomponent/chart-component.module';
import { GaugeComponentModule } from './gaugecomponent/gauge.component.module';
import { GaugeElementModule } from './gaugeelmentcomponent/gauge-element-component.module';
import {ReadOnlyElementModule} from './ReadOnlyElements/readonlyelements.module';

@NgModule({
    imports:[
    ScreenComponentModule,
    BodyComponentModule,  
    GridRowComponentModule,
    GridColComponentModule,
    PanelComponentModule,
    PanelSecComponentModule,
    FormComponentModule,
    SecRowComponentModule,
    SecColComponentModule,
    InputComponentModule,
    ButtonComponentModule,
    TableComponentModule,
    TextAreaModule,
    ListComponentModule,
    BadgeComponentModule,
    BulletComponentModule,
    BreadCrumbModule,
    BreadCrumbElementModule,
    CardNumberModule,
    ToggleModule,
    TextModule,
    TagsInputModule,
    StepperModule,
    SortCodeModule,
    SortCodeAccountModule,
    SliderComponentModule,
    SideBarModule,
    SeparatorComponentModule,
    RadioButtonComponentModule,
    ProgressStepComponentModule,
    ProgressBarComponentModule,
    PopOverModule,
    NavbarModule,
    LabelComponentModule,
    InputWithButtonComponentModule,
    ImageComponentModule,
    IconComponentModule,
    HyperLinkComponentModule,
    HeaderModule,
    FooterModule,
    FileBrowserModule,
    ExternalLinkComponentModule,
    CheckBoxComponentModule,
    DropDownButtonModule,
    DropDownModule,
    DialogModule,
    CheckBoxGroupModule,
    ModalModule,
    MenuModule,
    ChartModule,
    GaugeComponentModule,
    GaugeElementModule,
    ReadOnlyElementModule
    ],
    exports:[ScreenComponentModule,BodyComponentModule,GridRowComponentModule,GridColComponentModule,
        PanelComponentModule,PanelSecComponentModule,FormComponentModule,SecRowComponentModule,
        SecColComponentModule,InputComponentModule,ButtonComponentModule,TableComponentModule,SortCodeAccountModule,
        TextAreaModule,ListComponentModule,BulletComponentModule,CardNumberModule,ToggleModule,TextModule,TagsInputModule,StepperModule,SortCodeModule,
        SliderComponentModule,SideBarModule,SeparatorComponentModule,RadioButtonComponentModule,ProgressStepComponentModule,
        ProgressBarComponentModule,PopOverModule,NavbarModule,LabelComponentModule,InputWithButtonComponentModule,
        ImageComponentModule,IconComponentModule,BreadCrumbModule,BreadCrumbElementModule,HyperLinkComponentModule,HeaderModule,FooterModule,FileBrowserModule,ExternalLinkComponentModule,
        CheckBoxComponentModule,ModalModule,DropDownButtonModule,DropDownModule,DialogModule,CheckBoxGroupModule,BadgeComponentModule,MenuModule,ChartModule,GaugeComponentModule,GaugeElementModule,
        ReadOnlyElementModule]
})
export class AngularComponentModule{

}