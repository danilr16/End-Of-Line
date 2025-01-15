import "../static/css/components/pagination.css";
import classNames from "classnames";


const PaginationItem = ({ page, currentPage, onPageChange }) => {
const liClasses = classNames({
    "page-item": true,
    active: page === currentPage,
});
return (
    <div className={liClasses} onClick={() => onPageChange(page)}>
    <span className="page-link">{page}</span>
    </div>
);
};

const PaginationNext = ({currentPage,onPageChange,totalPages}) =>{
    const classes = classNames({
        "page-item": true,
        disabled: totalPages=== currentPage,
    });
    return (
        <div className={classes} onClick={() => {if(currentPage < totalPages) onPageChange(currentPage+1)}}>
        <span className="page-link">{">"}</span>
        </div>
    );
}

const PaginationLast = ({currentPage,onPageChange,totalPages}) =>{
    const classes = classNames({
        "page-item": true,
        disabled: totalPages=== currentPage,
    });
    return (
        <div className={classes} onClick={() => {if(currentPage < totalPages) onPageChange(totalPages)}}>
        <span className="page-link">{">>"}</span>
        </div>
    );
}

const PaginationPrev = ({currentPage,onPageChange}) =>{
    const classes = classNames({
        "page-item": true,
        disabled: 1 === currentPage,
    });
    return (
        <div className={classes} onClick={() => {if(currentPage - 1 > 0) onPageChange(currentPage-1)}}>
        <span className="page-link">{"<"}</span>
        </div>
    );
}

const PaginationFirst = ({currentPage,onPageChange}) =>{
    const classes = classNames({
        "page-item": true,
        disabled: 1 === currentPage,
    });
    return (
        <div className={classes} onClick={() => {if(currentPage - 1 > 0) onPageChange(1)}}>
        <span className="page-link">{"<<"}</span>
        </div>
    );
}


const Pagination = ({ currentPage, totalPages, onPageChange }) => {
return (
    <div className="pagination">

        <PaginationFirst currentPage={ currentPage} onPageChange={onPageChange}/> 
        <PaginationPrev currentPage={ currentPage} onPageChange={onPageChange}/> 

        {currentPage>1&& <PaginationItem
        page={currentPage -1}
        currentPage={currentPage}
        onPageChange={onPageChange}
        />}

        <PaginationItem
        page={currentPage}
        currentPage={currentPage}
        onPageChange={onPageChange}
        />

        {currentPage < totalPages&& <PaginationItem
        page={currentPage+1}
        currentPage={currentPage}
        onPageChange={onPageChange}
        />}

        <PaginationNext  currentPage={currentPage} onPageChange={onPageChange} totalPages = {totalPages}/>
        <PaginationLast currentPage={currentPage} onPageChange={onPageChange} totalPages = {totalPages}/>
    </div>
);
};
export default Pagination;

