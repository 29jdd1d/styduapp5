package com.studyapp.question.controller;

import com.studyapp.common.result.Result;
import com.studyapp.question.dto.CategoryTreeResponse;
import com.studyapp.question.dto.QuestionDetailResponse;
import com.studyapp.question.dto.QuestionResponse;
import com.studyapp.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题库控制器
 * <p>
 * 提供考研题库相关的接口，包括题目分类管理、题目查询等功能。
 * 支持按专业获取题目分类树、分页查询题目列表、获取题目详情以及随机抽题等操作。
 * </p>
 *
 * @author StudyApp
 * @since 1.0.0
 */
@Tag(name = "题库接口", description = "考研题库模块，提供题目分类、题目查询、随机抽题等功能")
@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 获取题目分类树
     * <p>
     * 根据专业ID获取该专业下的所有题目分类，以树形结构返回。
     * 分类树包含多级分类，每个分类节点包含分类ID、名称、父级ID及子分类列表。
     * </p>
     *
     * @param majorId 专业ID
     * @return 分类树列表
     */
    @Operation(
            summary = "获取题目分类树",
            description = "根据专业ID获取该专业下的所有题目分类，以树形结构返回。" +
                    "分类树支持多级嵌套，每个节点包含分类基本信息及其子分类列表。" +
                    "前端可根据此接口构建题目分类导航菜单。"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功，返回分类树列表"),
            @ApiResponse(responseCode = "400", description = "参数错误，专业ID不能为空"),
            @ApiResponse(responseCode = "404", description = "专业不存在或该专业下无题目分类")
    })
    @GetMapping("/category/tree")
    public Result<List<CategoryTreeResponse>> getCategoryTree(
            @Parameter(description = "专业ID，用于筛选该专业下的题目分类", required = true, example = "1")
            @RequestParam(name = "majorId") Long majorId) {
        return Result.success(questionService.getCategoryTree(majorId));
    }

    /**
     * 获取分类下的题目列表
     * <p>
     * 根据分类ID获取该分类下的所有题目，可选择是否包含子分类下的题目。
     * 返回的题目列表包含题目基本信息，如题目ID、题干、题型、难度等。
     * </p>
     *
     * @param categoryId      分类ID
     * @param includeChildren 是否包含子分类的题目
     * @return 题目列表
     */
    @Operation(
            summary = "获取分类下的题目列表",
            description = "根据分类ID获取该分类下的所有题目。" +
                    "支持通过includeChildren参数控制是否同时获取子分类下的题目。" +
                    "当includeChildren为true时，将递归获取所有子分类的题目；" +
                    "为false时仅获取当前分类直属的题目。"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功，返回题目列表"),
            @ApiResponse(responseCode = "400", description = "参数错误，分类ID不能为空"),
            @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @GetMapping("/list")
    public Result<List<QuestionResponse>> getQuestionList(
            @Parameter(description = "分类ID，用于筛选该分类下的题目", required = true, example = "10")
            @RequestParam(name = "categoryId") Long categoryId,
            @Parameter(description = "是否包含子分类的题目，true表示包含，false表示不包含，默认为false", example = "false")
            @RequestParam(name = "includeChildren", defaultValue = "false") Boolean includeChildren
    ) {
        return Result.success(questionService.getQuestionList(categoryId, includeChildren));
    }

    /**
     * 获取题目详情
     * <p>
     * 根据题目ID获取题目的完整信息，包括题干、选项、答案、解析等。
     * 此接口通常用于用户点击某道题目查看详情时调用。
     * </p>
     *
     * @param id 题目ID
     * @return 题目详情
     */
    @Operation(
            summary = "获取题目详情",
            description = "根据题目ID获取题目的完整详细信息。" +
                    "返回内容包括：题干内容、题目类型（单选/多选/判断/填空/简答等）、" +
                    "选项列表（选择题）、正确答案、答案解析、难度等级、所属分类等信息。" +
                    "适用于用户查看题目详情、做题、查看解析等场景。"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功，返回题目详情"),
            @ApiResponse(responseCode = "400", description = "参数错误，题目ID格式不正确"),
            @ApiResponse(responseCode = "404", description = "题目不存在")
    })
    @GetMapping("/detail/{id}")
    public Result<QuestionDetailResponse> getQuestionDetail(
            @Parameter(description = "题目ID", required = true, example = "100")
            @PathVariable Long id) {
        return Result.success(questionService.getQuestionDetail(id));
    }

    /**
     * 随机获取题目
     * <p>
     * 根据专业和分类随机获取指定数量的题目，用于随机练习、模拟测试等场景。
     * 支持仅指定专业（全专业随机）或同时指定分类（分类内随机）。
     * </p>
     *
     * @param majorId    专业ID
     * @param categoryId 分类ID（可选）
     * @param count      获取数量
     * @return 随机题目列表
     */
    @Operation(
            summary = "随机获取题目",
            description = "根据指定条件随机获取题目，用于随机练习、每日一练、模拟测试等场景。" +
                    "必须指定专业ID，可选指定分类ID进行更精确的范围限定。" +
                    "当不指定分类ID时，将从该专业下所有题目中随机抽取；" +
                    "指定分类ID时，仅从该分类（含子分类）中随机抽取。" +
                    "count参数控制返回题目数量，默认为10道，建议不超过50道。"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功，返回随机题目列表"),
            @ApiResponse(responseCode = "400", description = "参数错误，专业ID不能为空或数量参数不合法"),
            @ApiResponse(responseCode = "404", description = "专业或分类不存在，或符合条件的题目不足")
    })
    @GetMapping("/random")
    public Result<List<QuestionResponse>> getRandomQuestions(
            @Parameter(description = "专业ID，限定随机题目的专业范围", required = true, example = "1")
            @RequestParam(name = "majorId") Long majorId,
            @Parameter(description = "分类ID，可选参数，用于进一步限定随机题目的分类范围，不传则从整个专业范围内随机", example = "10")
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @Parameter(description = "获取题目数量，默认为10道，建议范围1-50", example = "10")
            @RequestParam(name = "count", defaultValue = "10") Integer count
    ) {
        return Result.success(questionService.getRandomQuestions(majorId, categoryId, count));
    }
}
