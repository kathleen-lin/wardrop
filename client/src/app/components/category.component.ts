import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Item } from '../model';
import { ItemService } from '../item.service';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css']
})
export class CategoryComponent implements OnInit {

  // get this using ativated route
  categoryName!: string

  items!: Item[]

  constructor(private httpClient: HttpClient, private activatedRoute: ActivatedRoute, private itmSvc: ItemService) {}

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(
      async (params)=> {
        this.categoryName = params['name'];
        // this.fileUpSvc.getImage(this.postId)
        //   .then((result)=>{
        //     this.imageData = result.image;
        //     console.log(this.imageData);
        // });
        console.log(">>>" + this.categoryName);        
      }
    );
    // query for list of items with category, fetch from: http://localhost:8080/api?category=top
    this.itmSvc.getItemListByCategory(this.categoryName)
      .then((r) => {
        this.items = r
        console.log(this.items)
      })
      .catch((err) => console.log(err))
  }

  increaseWorn(item: Item) {
    item.timeWorn++
    // update the database
    this.itmSvc.increaseTimeWorn(item.itemId)
      .then((r) => console.log(r))
      .catch((err) => console.log(err))
  }

}
