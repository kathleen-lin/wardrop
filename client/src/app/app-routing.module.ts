import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home.component';
import { CategoryComponent } from './components/category.component';
import { ItemComponent } from './components/item.component';
import { AddItemComponent } from './components/add-item.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'category/:name', component: CategoryComponent },
  { path: 'item/:itemId', component: ItemComponent },
  { path: 'add', component: AddItemComponent },
  { path: "**", redirectTo:'/', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
