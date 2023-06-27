import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Item } from '../model';
import { ItemService } from '../item.service';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css']
})
export class CategoryComponent implements OnInit {

  // get this using ativated route
  categoryName!: string

  items!: Item[]

  user!: string|null

  isListEmpty: boolean = false

  sortForm!: FormGroup

  constructor(private httpClient: HttpClient, private activatedRoute: ActivatedRoute, private itmSvc: ItemService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(
      async (params)=> {
        this.categoryName = params['name'];
        // this.fileUpSvc.getImage(this.postId)
        //   .then((result)=>{
        //     this.imageData = result.image;
        //     console.log(this.imageData);
        // });
        // console.log(">>>" + this.categoryName);        
      })
      this.sortForm = this.fb.group(
        {sortBy: this.fb.control<string>(''),
          orderBy: this.fb.control<string>('')}
      )
    ;
    this.user = localStorage.getItem("user")
    if (this.user != null) {

    // console.log("user: " + this.user);
    // query for list of items with category, fetch from: http://localhost:8080/api?category=top?user=celine
    // @ts-ignore
    this.itmSvc.getItemListByCategory(this.categoryName, this.user)
      .then((r) => {
        this.items = r
        if (this.items.length == 0) {
          this.isListEmpty = true
          console.log("nothing in category")
        }
        console.log(this.items)

      })
      .catch((err) => console.log(err))
    }
  }

  increaseWorn(item: Item) {
    item.timeWorn++
    // update the database
    this.itmSvc.increaseTimeWorn(item.itemId)
      .then((r) => console.log(r))
      .catch((err) => console.log(err))
  }

  getSorted(){
    const sortBy = this.sortForm.get('sortBy')?.value
    const orderBy = this.sortForm.get('orderBy')?.value
    // @ts-ignore
    this.itmSvc.getSortResult(sortBy, orderBy, this.categoryName, this.user)
    .then((r) => {
      // @ts-ignore
      this.items = r
      if (this.items.length == 0) {
        this.isListEmpty = true
        // console.log("nothing in category")
      }
      // console.log(this.items)
    })
    .catch((err) => console.log(err))
  
  }

}
