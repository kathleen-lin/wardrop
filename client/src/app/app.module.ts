import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home.component';
import { CategoryComponent } from './components/category.component';
import { AddItemComponent } from './components/add-item.component';
import { ItemComponent } from './components/item.component';

import { HttpClientModule } from '@angular/common/http'
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
// import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import {MatDialogModule} from '@angular/material/dialog';
import { RemovalComponent } from './components/removal.component';
import { AddItemImageComponent } from './components/add-item-image.component';
import { AnalyseComponent } from './components/analyse.component';
import { DriveComponent } from './components/drive.component';
import { AddOOTDComponent } from './components/add-ootd.component';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';


@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    CategoryComponent,
    AddItemComponent,
    ItemComponent,
    RemovalComponent,
    AddItemImageComponent,
    AnalyseComponent,
    DriveComponent,
    AddOOTDComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatSlideToggleModule,
    MatDialogModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule
    // BrowserAnimationsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
