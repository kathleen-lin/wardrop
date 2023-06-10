import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home.component';
import { CategoryComponent } from './components/category.component';
import { ItemComponent } from './components/item.component';
import { AddItemComponent } from './components/add-item.component';
import { AddItemImageComponent } from './components/add-item-image.component';
import { AnalyseComponent } from './components/analyse.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'category/:name', component: CategoryComponent },
  { path: 'item/:itemId', component: ItemComponent },
  { path: 'add', component: AddItemImageComponent },
  { path: 'add/details', component: AddItemComponent },
  { path: 'analyse/:filename', component: AnalyseComponent },
  { path: "**", redirectTo:'/', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
