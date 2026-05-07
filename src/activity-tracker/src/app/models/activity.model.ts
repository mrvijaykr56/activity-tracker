import { Category } from "./category.enum";
import { Day } from "./day.enum";

export interface Activity {
    id?: number;
    activityName: string;
    category: Category;
    timeDuration: string;
    date: string;
    days: Day;
    user?: any;
}